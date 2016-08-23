package demo

import validation.Result.{ Invalid, Invalids, Valid }

import org.scalatest.{ FlatSpec, Inside, Matchers }


object FromMapSpec {
  case class Address(street: String, zip: Int)
  case class Person(name: String, addresses: List[Address])
}

final class FromMapSpec extends FlatSpec with Inside with Matchers {
  import FromMapSpec._
  val mapper = FromMap.to[Person]

  behavior of "FromMap"

  it should "build a person" in {

    val mp = Map[String, Any](
      "name" -> "Tom",
      "addresses" -> List(Map[String, Any]("street" -> "Jefferson st", "zip" -> 10000))
    )

    inside(mapper(mp)) {
      case Valid(person) =>
        person.name shouldBe "Tom"
        person.addresses should have size 1
        person.addresses.head.street shouldBe "Jefferson st"
        person.addresses.head.zip shouldBe 10000
    }
  }

  it should "only accept List, not Vector for nested lists of maps" in {

    val mp = Map[String, Any](
      "name" -> "Tom",
      "addresses" -> Vector(Map[String, Any]("street" -> "Jefferson st", "zip" -> 10000))
    )

    inside(mapper(mp)) {
      case Invalid(error) =>
        error shouldBe WrongType("addresses", "nested list of maps")
    }
  }

  it should "report a missing name entry" in {
    val mp = Map[String, Any](
      "names" -> "Tom",
      "addresses" -> List(Map[String, Any]("street" -> "Jefferson st", "zip" -> 10000))
    )

    inside(mapper(mp)) {
      case Invalid(error) =>
        error shouldBe Missing("name")
    }
  }

  it should "report a missing addresses entry" in {

    val mp = Map[String, Any](
      "name" -> "Tom",
      "address" -> Map("street" -> "Jefferson st", "zip" -> 10000)
    )

    inside(mapper(mp)) {
      case Invalid(error) =>
        error shouldBe Missing("addresses")
    }
  }

  it should "not accept a map when a list is required" in {

    val mp = Map[String, Any](
      "name" -> "Tom",
      "addresses" -> Map("streets" -> "Jefferson st", "zip" -> "10000")
    )

    inside(mapper(mp)) {
      case Invalid(error) =>
        error shouldBe WrongType("addresses", "nested list of maps")
    }
  }

  it should "report multiple nested errors" in {
    val mp = Map(
      "name" -> "Tom",
      "addresses" -> List(Map("streets" -> "Jefferson st", "zip" -> "10000"))
    )

    inside(mapper(mp)) {
      case Invalids(es) =>
        val errors = es.toVector
        errors should have size 2
        errors(0) shouldBe Missing("addresses.0.street")
        errors(1) shouldBe Mistyped[Int]("addresses.0.zip", "10000")
    }
  }

  it should "report type mismatch if some other elements are correct" in {
    val mp = Map(
      "name" -> "Tom",
      "addresses" -> List(Map[String, Any]("street" -> "Jefferson st", "zip" -> 10000), "foo")
    )

    inside(mapper(mp)) {
      case Invalid(error) =>
        error shouldBe WrongType("addresses", "nested list of maps")
    }
  }

  it should "accumulate all errors" in {
    val mp = Map(
      "name" -> 't',
      "addresses" -> List(
        Map[String, Any]("streets" -> "Jefferson st", "zip" -> "10000"),
        Map[String, Any]("street" -> 'b', "sip" -> 10000))
    )

    inside(mapper(mp)) {
      case Invalids(es) =>
        val errors = es.toVector
        errors should have size 5
        errors(0) shouldBe Mistyped[String]("name", 't')
        errors(1) shouldBe Missing("addresses.0.street")
        errors(2) shouldBe Mistyped[Int]("addresses.0.zip", "10000")
        errors(3) shouldBe Mistyped[String]("addresses.1.street", 'b')
        errors(4) shouldBe Missing("addresses.1.zip")
    }
  }
}
