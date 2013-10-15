/**
 * Generated by Scrooge
 *   version: ?
 *   rev: ?
 *   built at: ?
 */
package the.walrus.ckite.rpc.thrift

import com.twitter.scrooge.{
  ThriftException, ThriftStruct, ThriftStructCodec3}
import org.apache.thrift.protocol._
import org.apache.thrift.transport.{TMemoryBuffer, TTransport}
import java.nio.ByteBuffer
import scala.collection.immutable.{Map => immutable$Map}
import scala.collection.mutable.{
  ArrayBuffer => mutable$ArrayBuffer, Buffer => mutable$Buffer,
  HashMap => mutable$HashMap, HashSet => mutable$HashSet}
import scala.collection.{Map, Set}


object RequestVoteST extends ThriftStructCodec3[RequestVoteST] {
  val Struct = new TStruct("RequestVoteST")
  val MemberIdField = new TField("memberId", TType.STRING, 1)
  val MemberIdFieldManifest = implicitly[Manifest[String]]
  val TermField = new TField("term", TType.I32, 2)
  val TermFieldManifest = implicitly[Manifest[Int]]
  val LastLogIndexField = new TField("lastLogIndex", TType.I32, 3)
  val LastLogIndexFieldManifest = implicitly[Manifest[Int]]
  val LastLogTermField = new TField("lastLogTerm", TType.I32, 4)
  val LastLogTermFieldManifest = implicitly[Manifest[Int]]

  /**
   * Checks that all required fields are non-null.
   */
  def validate(_item: RequestVoteST) {
    if (_item.memberId == null) throw new TProtocolException("Required field memberId cannot be null")
  }

  override def encode(_item: RequestVoteST, _oproto: TProtocol) { _item.write(_oproto) }
  override def decode(_iprot: TProtocol): RequestVoteST = Immutable.decode(_iprot)

  def apply(
    memberId: String,
    term: Int,
    lastLogIndex: Int = -1,
    lastLogTerm: Int = -1
  ): RequestVoteST = new Immutable(
    memberId,
    term,
    lastLogIndex,
    lastLogTerm
  )

  def unapply(_item: RequestVoteST): Option[Product4[String, Int, Int, Int]] = Some(_item)

  object Immutable extends ThriftStructCodec3[RequestVoteST] {
    override def encode(_item: RequestVoteST, _oproto: TProtocol) { _item.write(_oproto) }
    override def decode(_iprot: TProtocol): RequestVoteST = {
      var memberId: String = null
      var _got_memberId = false
      var term: Int = 0
      var _got_term = false
      var lastLogIndex: Int = -1
      var _got_lastLogIndex = false
      var lastLogTerm: Int = -1
      var _got_lastLogTerm = false
      var _done = false
      _iprot.readStructBegin()
      while (!_done) {
        val _field = _iprot.readFieldBegin()
        if (_field.`type` == TType.STOP) {
          _done = true
        } else {
          _field.id match {
            case 1 => { /* memberId */
              _field.`type` match {
                case TType.STRING => {
                  memberId = {
                    _iprot.readString()
                  }
                  _got_memberId = true
                }
                case _ => TProtocolUtil.skip(_iprot, _field.`type`)
              }
            }
            case 2 => { /* term */
              _field.`type` match {
                case TType.I32 => {
                  term = {
                    _iprot.readI32()
                  }
                  _got_term = true
                }
                case _ => TProtocolUtil.skip(_iprot, _field.`type`)
              }
            }
            case 3 => { /* lastLogIndex */
              _field.`type` match {
                case TType.I32 => {
                  lastLogIndex = {
                    _iprot.readI32()
                  }
                  _got_lastLogIndex = true
                }
                case _ => TProtocolUtil.skip(_iprot, _field.`type`)
              }
            }
            case 4 => { /* lastLogTerm */
              _field.`type` match {
                case TType.I32 => {
                  lastLogTerm = {
                    _iprot.readI32()
                  }
                  _got_lastLogTerm = true
                }
                case _ => TProtocolUtil.skip(_iprot, _field.`type`)
              }
            }
            case _ =>
              TProtocolUtil.skip(_iprot, _field.`type`)
          }
          _iprot.readFieldEnd()
        }
      }
      _iprot.readStructEnd()
      if (!_got_memberId) throw new TProtocolException("Required field 'RequestVoteST' was not found in serialized data for struct RequestVoteST")
      if (!_got_term) throw new TProtocolException("Required field 'RequestVoteST' was not found in serialized data for struct RequestVoteST")
      new Immutable(
        memberId,
        term,
        lastLogIndex,
        lastLogTerm
      )
    }
  }

  /**
   * The default read-only implementation of RequestVoteST.  You typically should not need to
   * directly reference this class; instead, use the RequestVoteST.apply method to construct
   * new instances.
   */
  class Immutable(
    val memberId: String,
    val term: Int,
    val lastLogIndex: Int = -1,
    val lastLogTerm: Int = -1
  ) extends RequestVoteST

  /**
   * This Proxy trait allows you to extend the RequestVoteST trait with additional state or
   * behavior and implement the read-only methods from RequestVoteST using an underlying
   * instance.
   */
  trait Proxy extends RequestVoteST {
    protected def _underlying_RequestVoteST: RequestVoteST
    override def memberId: String = _underlying_RequestVoteST.memberId
    override def term: Int = _underlying_RequestVoteST.term
    override def lastLogIndex: Int = _underlying_RequestVoteST.lastLogIndex
    override def lastLogTerm: Int = _underlying_RequestVoteST.lastLogTerm
  }
}

trait RequestVoteST extends ThriftStruct
  with Product4[String, Int, Int, Int]
  with java.io.Serializable
{
  import RequestVoteST._


  def memberId: String
  def term: Int
  def lastLogIndex: Int
  def lastLogTerm: Int

  def _1 = memberId
  def _2 = term
  def _3 = lastLogIndex
  def _4 = lastLogTerm


  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is subtracked
   * from the passthroughFields map, if present.
   */
  def unsetField(_fieldId: Short): RequestVoteST =
    _fieldId match {
      case 1 => copy(memberId = null)
      case 2 => copy(term = 0)
      case 3 => copy(lastLogIndex = -1)
      case 4 => copy(lastLogTerm = -1)
      case _ => this
    }

  override def write(_oprot: TProtocol) {
    RequestVoteST.validate(this)
    _oprot.writeStructBegin(Struct)
    if (memberId ne null) {
      val memberId_item = memberId
      _oprot.writeFieldBegin(MemberIdField)
      _oprot.writeString(memberId_item)
      _oprot.writeFieldEnd()
    }
    if (true) {
      val term_item = term
      _oprot.writeFieldBegin(TermField)
      _oprot.writeI32(term_item)
      _oprot.writeFieldEnd()
    }
    if (true) {
      val lastLogIndex_item = lastLogIndex
      _oprot.writeFieldBegin(LastLogIndexField)
      _oprot.writeI32(lastLogIndex_item)
      _oprot.writeFieldEnd()
    }
    if (true) {
      val lastLogTerm_item = lastLogTerm
      _oprot.writeFieldBegin(LastLogTermField)
      _oprot.writeI32(lastLogTerm_item)
      _oprot.writeFieldEnd()
    }
    _oprot.writeFieldStop()
    _oprot.writeStructEnd()
  }

  def copy(
    memberId: String = this.memberId, 
    term: Int = this.term, 
    lastLogIndex: Int = this.lastLogIndex, 
    lastLogTerm: Int = this.lastLogTerm
  ): RequestVoteST =
    new Immutable(
      memberId,
      term,
      lastLogIndex,
      lastLogTerm
    )

  override def canEqual(other: Any): Boolean = other.isInstanceOf[RequestVoteST]

  override def equals(other: Any): Boolean =
    _root_.scala.runtime.ScalaRunTime._equals(this, other)

  override def hashCode: Int = _root_.scala.runtime.ScalaRunTime._hashCode(this)

  override def toString: String = _root_.scala.runtime.ScalaRunTime._toString(this)


  override def productArity: Int = 4

  override def productElement(n: Int): Any = n match {
    case 0 => memberId
    case 1 => term
    case 2 => lastLogIndex
    case 3 => lastLogTerm
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def productPrefix: String = "RequestVoteST"
}