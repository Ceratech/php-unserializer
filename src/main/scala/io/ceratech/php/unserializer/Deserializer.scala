package io.ceratech.php.unserializer

import java.nio.charset.Charset

import scala.io.Source

/**
  * PHP serialized data deserializer
  *
  * @param source input source that contains serialized PHP string
  */
private[unserializer] class PHPSParser(source: Source) {

  /**
    * @return parsed result
    */
  def parse: Any = {
    if (!source.hasNext) {
      None
    } else {
      source.next match {
        case 'i' ⇒ parseInt()
        case 'd' ⇒ parseDouble()
        case 's' ⇒ parseString()
        case 'b' ⇒ parseBool()
        case 'N' ⇒ parseNull
        case 'a' ⇒ parseArray()
        case 'O' ⇒ parseObject()
        case x ⇒ throw PHPSException(s"Unexpected character: $x")
      }
    }
  }

  private lazy val parseInt = parser(until(';').toInt)

  private lazy val parseDouble = parser(until(';').toDouble)

  private lazy val parseBool = parser {
    val b = source.next() match {
      case '0' ⇒ false
      case '1' ⇒ true
    }
    discard(';')
    b
  }

  private lazy val parseString = parser {
    val s = stringBody
    discard(';')
    s
  }

  private def stringBody = {
    val l = parseLength
    discard('"')
    val s = source.take(l).mkString
    discard('"')
    s
  }

  private def parseNull = {
    discard(';')
    None
  }

  private lazy val parseArray = parser {
    val vs = arrayBody
    simpleArray(vs) match {
      case Some(a) ⇒ a
      case None ⇒ Map(vs.toList: _*)
    }
  }

  private def arrayBody = {
    val l = parseLength
    discard('{')
    val vs = (0 until l).map(x ⇒ parseAssoc)
    discard('}')
    vs
  }

  private def simpleArray(vs: IndexedSeq[(Any, Any)]): Option[List[Any]] = {
    val xs = for (i <- vs.indices; if vs(i)._1 == i)
      yield vs(i)._2
    if (xs.length == vs.length) Some(xs.toList) else None
  }

  private def parseAssoc = parse → parse

  private lazy val parseObject = parser {
    val name = stringBody
    discard(':')
    val fields = arrayBody
    val (values, access) = parseFields(fields)
    new PHPObject(name, values, access)
  }

  private def parseFields(fields: IndexedSeq[(Any, Any)]): (Map[String, Any], Map[String, Access]) = {
    import PHPSParser.FieldName

    var vs = List[(String, Any)]()
    var as = List[(String, Access)]()

    fields.foreach {
      case (k: String, v) =>
        val (access, name) = k match {
          case FieldName(fAccess, fName) => fAccess match {
            case "*" => Protected -> fName
            case _ => Private -> fName
          }
          case name: String => Public -> name
        }
        vs = name -> v :: vs
        as = name -> access :: as
      case (k, _) ⇒ throw PHPSException(s"Object field key not a string but: $k")
    }
    Map(vs: _*) → Map(as: _*)
  }

  private def parseLength = until(':').toInt

  private def until(c: Char): String = source.takeWhile(_ != c).mkString

  private def parser[T](func: ⇒ T) = () ⇒ {
    discard(':')
    func
  }

  private def discard(c: Char) {
    if (!source.hasNext) {
      throw PHPSException(s"Unexpected EOF. Expected $c.")
    }
    source.next match {
      case x: Char if x == c ⇒ ()
      case x ⇒ throw PHPSException(s"Expected $c but found $x")
    }
  }
}

object PHPSParser {

  /** PHP field name regex */
  private val FieldName = "\0(.*)\0(.*)".r

  /**
    * Unserialize a PHP serialized string. Any errors will be thrown; otherwise a parsed result will be returned
    *
    * @param data the serialized string
    * @return parsed data
    */
  def unserialize(data: String, encoding: Charset = Charset.forName("UTF-8")): Any = {
    unserialize(Source.fromString(new String(data.getBytes(encoding), "ISO-8859-1")))
  }

  /**
   * Unserialize the provided data in the given source. Any errors will be thrown; otherwise the parsed result will be returned
   *
   * @param source the source of the serialized data
   * @return parsed data
   */
  def unserialize(source: Source) = new PHPSParser(source).parse
}

/**
  * Exception raised when there are parser errors.
  */
case class PHPSException(message: String) extends Exception(message)

sealed trait Access

case object Public extends Access

case object Private extends Access

case object Protected extends Access


/**
  * Represents a serialized PHP object instance.
  */
class PHPObject(val className: String, val fields: Map[String, Any], val fieldAccess: Map[String, Access])