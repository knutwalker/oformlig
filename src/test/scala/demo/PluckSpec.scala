package demo

import org.scalatest.{ FlatSpec, Matchers }
import shapeless.test.{ illTyped, typed }

object PluckSpec extends Matchers {
  case class Foo(foo: String, bar: Int, baz: Boolean)
}
final class PluckSpec extends FlatSpec with Matchers {
  import PluckSpec._
  import Pluck.syntax
  val xs = List(Foo("f", 42, baz = true), Foo("g", 1337, baz = false))

  behavior of "Pluck"

  it should "get all 'foo' items with proper type" in {
    import cats.std.list._

    val foos = xs pluck "foo"

    foos should have size 2

    foos(0) shouldBe "f"
    typed[String](foos(0))

    foos(1) shouldBe "g"
    typed[String](foos(1))
  }

  it should "get all 'bar' items with proper type" in {
    import cats.std.list._

    val foos = xs pluck "bar"

    foos should have size 2

    foos(0) shouldBe 42
    typed[Int](foos(0))

    foos(1) shouldBe 1337
    typed[Int](foos(1))
  }

  it should "get all 'baz' items with proper type" in {
    import cats.std.list._

    val foos = xs pluck "baz"

    foos should have size 2

    foos(0) shouldBe true
    typed[Boolean](foos(0))

    foos(1) shouldBe false
    typed[Boolean](foos(1))
  }

  it should "work for everything that has a functor instance" in {
    import cats.std.option._

    val foo = Option(Foo("bernd", 42, true)) pluck "foo"

    foo shouldBe Some("bernd")
    typed[String](foo.get)
  }

  it should "work for shapeless derived functor instances" in {
    import cats.derived.functor._
    import cats.derived.functor.legacy._

    case class Bar[A](xs: List[A], s: Option[A])
    val bar = Bar(
      List(
        Foo("bernd", 42, baz = true),
        Foo("ralle", 1337, baz = false)
      ),
      Some(
        Foo("bippy", 12, true)
      )) pluck "foo"

    bar shouldBe Bar(List("bernd", "ralle"), Some("bippy"))

    bar.xs(0) shouldBe "berd"
    typed[String](bar.xs(0))

    bar.xs(1) shouldBe "ralle"
    typed[String](bar.xs(1))

    bar.s.get shouldBe "bippy"
    typed[String](bar.s.get)
  }

  it should "fail to compile when field does not exist" in {
    import cats.std.list._

    illTyped("xs.pluck(\"blubb\")",
      ".*Foo does not have a field \"blubb\"\\.")
  }

  it should "fail to compile when target type does not conform to field type" in {
    import cats.std.list._

    illTyped("xs.pluck(\"foo\"): List[Int]",
      "type mismatch.*found.*List\\[String\\].*required.*List\\[Int\\]")
  }
}
