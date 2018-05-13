package io.ceratech.php.unserializer

import org.scalatest.{MustMatchers, WordSpec}

import scala.io.Source

/**
  * Deserializer tests
  */
class PHPSParserSpec extends WordSpec with MustMatchers {

  private def fixture(name: String) = getClass.getResourceAsStream("/fixtures/%s.phps".format(name))

  private def parse(name: String) = new PHPSParser(Source.fromInputStream(fixture(name))).parse

  "the PHPS parser" should {

    "parse integers" in {
      parse("int") must be(3)
    }

    "parse large integers" in {
      parse("biggerint") must be(252873459)
    }

    "parse negative integers" in {
      parse("negativeint") must be(-70)
    }

    "parse doubles" in {
      parse("overflowint") must be(6442450941.0)
    }

    "parse strings" in {
      parse("string") must be("foo")
    }

    "parse empty strings" in {
      parse("empty-string") must be("")
    }

    "parse true booleans" in {
      parse("bool") == true must be(true)
    }

    "parse false booleans" in {
      parse("bool-false") == false must be(true)
    }

    "parse nulls" in {
      parse("null") == None must be(true)
    }

    "parse empty arrays" in {
      parse("empty-array") must be(Nil)
    }

    "parse simple numbered arrays as lists" in {
      parse("array") must be(List(1, 2, 3))
    }

    "parse unordered numbered arrays as maps" in {
      parse("tricky-array") must be(Map(0 -> 1, 3 -> 2))
    }

    "return associative arrays as maps" in {
      parse("assoc-array") must be(Map("foo" -> 1, "bar" -> 2))
    }

    "return arrays with mixed key types as maps" in {
      parse("mixed-array") must be(Map(1 -> "a", "foo" -> "bar"))
    }

    def phpobj = parse("object").asInstanceOf[PHPObject]

    "parse object names" in {
      phpobj.className must be("Blah")
    }

    "parse object fields" in {
      phpobj.fields("bar") must be(2)
    }

    "correctly parse public access" in {
      phpobj.fieldAccess("foo") must be(Private)
    }

    "correctly parse private access" in {
      phpobj.fieldAccess("bar") must be(Protected)
    }

    "correctly parse protected access" in {
      phpobj.fieldAccess("baz") must be(Public)
    }

    "throw an exception on invalid serialized data" in {
      val input = "s:4:\"ab\";"
      an[PHPSException] must be thrownBy PHPSParser.unserialize(input)
    }
  }
}