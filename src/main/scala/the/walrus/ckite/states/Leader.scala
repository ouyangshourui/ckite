package the.walrus.ckite.states

import java.util.concurrent.Executors
import java.util.concurrent.Future
import scala.util.Try
import the.walrus.ckite.Cluster
import the.walrus.ckite.rpc.Command
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions._
import the.walrus.ckite.executions.Executions
import the.walrus.ckite.rpc.RequestVoteResponse
import the.walrus.ckite.util.Logging
import the.walrus.ckite.rpc.AppendEntriesResponse
import the.walrus.ckite.rpc.RequestVote
import the.walrus.ckite.rpc.AppendEntries
import the.walrus.ckite.rpc.LogEntry
import the.walrus.ckite.RLog
import the.walrus.ckite.executions.Executions

/**
 * 	•! Initialize nextIndex for each to last log index + 1
 * •! Send initial empty AppendEntries RPCs (heartbeat) to each
 * follower; repeat during idle periods to prevent election
 * timeouts (§5.2)
 * •! Accept commands from clients, append new entries to local
 * log (§5.3)
 * •! Whenever last log index ! nextIndex for a follower, send
 * AppendEntries RPC with log entries starting at nextIndex,
 * update nextIndex if successful (§5.3)
 * •! If AppendEntries fails because of log inconsistency,
 * decrement nextIndex and retry (§5.3)
 * •! Mark entries committed if stored on a majority of servers
 * and some entry from current term is stored on a majority of
 * servers. Apply newly committed entries to state machine.
 * •! Step down if currentTerm changes (§5.5)
 */
case object Leader extends State {

  override def begin(term: Int)(implicit cluster: Cluster) = {
    if (term < cluster.local.term) {
      LOG.info(s"Cant be a Leader of term $term. Current term is ${cluster.local.term}")
      cluster.local.becomeFollower(cluster.local.term)
    } else {
      LOG.info(s"Start being Leader in term $term")
      cluster.updateLeader(cluster.local.id)
      resetLastLog
      resetNextIndexes
      Heartbeater start term
    }
  }

  private def resetLastLog(implicit cluster: Cluster) = {
    RLog.resetLastLog()
  }

  private def resetNextIndexes(implicit cluster: Cluster) = {
    val nextIndex = RLog.lastLog.intValue() + 1
    cluster.members.foreach { member => member.setNextLogIndex(nextIndex) }
  }

  override def stop(implicit cluster: Cluster) = {
    LOG.info("Stop being Leader. Stop heartbeats")
    cluster.setNoLeader()
    Heartbeater stop
  }

  override def onCommandReceived(command: Command)(implicit cluster: Cluster) = {
    val logEntry = LogEntry(cluster.local.term, RLog.nextLogIndex, command)
    RLog.append(List(logEntry))
    LOG.info(s"Replicating log entry $logEntry")
    val replicationAcks = Replicator.replicate(appendEntriesFor(logEntry))
    LOG.info(s"Got $replicationAcks replication acks")
    if (replicationAcks + 1 >= cluster.majority) {
      RLog.commit(logEntry)
    } else {
      LOG.info("Uncommited entry due to no majority")
    }
  }
  
  private def appendEntriesFor(logEntry: LogEntry)(implicit cluster: Cluster) = {
    val previousLogEntry = RLog.getPreviousLogEntry(logEntry)
    previousLogEntry match {
      case None => AppendEntries(cluster.local.term, cluster.local.id, RLog.getCommitIndex, entries = List(logEntry))
      case Some(entry) => AppendEntries(cluster.local.term, cluster.local.id, RLog.getCommitIndex, entry.index, entry.term, List(logEntry))
    }
  }

  override def onAppendEntriesReceived(appendEntries: AppendEntries)(implicit cluster: Cluster): AppendEntriesResponse = {
    if (appendEntries.term < cluster.local.term) {
      AppendEntriesResponse(cluster.local.term, false)
    } else {
      stepDown(Some(appendEntries.leaderId), appendEntries.term)
      cluster.local.onAppendEntriesReceived(appendEntries)
    }
  }

  override def onRequestVoteReceived(requestVote: RequestVote)(implicit cluster: Cluster): RequestVoteResponse = {
    if (requestVote.term <= cluster.local.term) {
      RequestVoteResponse(cluster.local.term, false)
    } else {
      stepDown(None, requestVote.term)
      cluster.local.onMemberRequestingVoteReceived(requestVote)
    }
  }

}

case object Replicator {

  val Name = "Replicator"
  val ReplicateTimeout = 1000
  val executor = Executors.newCachedThreadPool()
  
  //replicate and wait for a majority of acks. 
  def replicate(appendEntries: AppendEntries)(implicit cluster: Cluster): Int = {
    val execution = Executions.newExecution().withExecutor(executor)
    cluster.members.foreach { member =>
      execution.withTask(new Callable[Boolean] {
        override def call(): Boolean = {
          Thread.currentThread().setName(Name)
          member.replicate(appendEntries)
        }
      })
    }
    val results: Iterable[Boolean] = execution.withTimeout(ReplicateTimeout, TimeUnit.MILLISECONDS)
      .withExpectedResults(cluster.majority - 1)
      .execute[Boolean]()
    results.filter { result => result }.size
  }

}

case object Heartbeater extends Logging {

  val Name = "Heartbeater"
  val executor = Executors.newFixedThreadPool(1)
  val heartbeatFuture = new AtomicReference[Future[_]]()

  def start(term: Int)(implicit cluster: Cluster) = {
    heartbeatFuture.set(executor.submit(new Runnable() {
      override def run() = {
        try {
          cluster.updateContextInfo()
          Thread.currentThread().setName(Name)
          while (true) {
            //detectar si tengo contacto con todos, la mayoria o no importa?
            cluster.broadcastHeartbeats(term)
            Thread.sleep(cluster.configuration.heartbeatsInterval)
          }
        } catch { case e: Exception => LOG.debug("Heartbeats interrupted") }
      }
    }))
  }

  def stop() = {
    val future = heartbeatFuture.get()
    if (future != null) future.cancel(true)
  }
}