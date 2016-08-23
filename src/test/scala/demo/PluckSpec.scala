package demo

import org.scalatest.{ FlatSpec, Matchers }
import shapeless.test.{ illTyped, typed }

object PluckSpec extends Matchers {
  case class Foo(foo: String, bar: Int, baz: Boolean)
}
final class PluckSpec extends FlatSpec with Matchers {
  import PluckSpec._
  val xs = List(Foo("f", 42, baz = true), Foo("g", 1337, baz = false))

  behavior of "Pluck"

  it should "get all 'foo' items with proper type" in {

    val foos = Pluck.pluck(xs, "foo")

    foos should have size 2

    foos(0) shouldBe "f"
    typed[String](foos(0))

    foos(1) shouldBe "g"
    typed[String](foos(1))
  }

  it should "get all 'bar' items with proper type" in {

    val foos = Pluck.pluck(xs, "bar")

    foos should have size 2

    foos(0) shouldBe 42
    typed[Int](foos(0))

    foos(1) shouldBe 1337
    typed[Int](foos(1))
  }

  it should "get all 'baz' items with proper type" in {

    val foos = Pluck.pluck(xs, "baz")

    foos should have size 2

    foos(0) shouldBe true
    typed[Boolean](foos(0))

    foos(1) shouldBe false
    typed[Boolean](foos(1))
  }

  it should "fail to compile when field does not exist" in {
    illTyped("Pluck.pluck(xs, \"blubb\")",
      ".*Foo does not have a field String\\(\"blubb\"\\)\\.")
  }

  it should "fail to compile when target type does not conform to field type" in {
    illTyped("Pluck.pluck(xs, \"foo\"): List[Int]",
      "type mismatch.*found.*List\\[String\\].*required.*List\\[Int\\]")
  }
}
