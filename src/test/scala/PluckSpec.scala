import org.scalatest.{ FlatSpec, Matchers }

object PluckSpec extends Matchers {
  case class Foo(foo: String, bar: Int, baz: Boolean)

  implicit final class TypedOps[A](private val x: A) extends AnyVal {
    def shouldTypeEqual(y: A) = x shouldBe y
  }
}
final class PluckSpec extends FlatSpec with Matchers {
  import PluckSpec._
  val xs = List(Foo("f", 42, baz = true), Foo("g", 1337, baz = false))

  behavior of "Pluck"

  it should "get all 'foo' items with proper type" in {

    val foos = Pluck.pluck(xs, "foo")

    foos should have size 2
    foos(0) shouldTypeEqual "f"
    foos(1) shouldTypeEqual "g"
  }

  it should "get all 'bar' items with proper type" in {

    val foos = Pluck.pluck(xs, "bar")

    foos should have size 2
    foos(0) shouldTypeEqual 42
    foos(1) shouldTypeEqual 1337
  }

  it should "get all 'baz' items with proper type" in {

    val foos = Pluck.pluck(xs, "baz")

    foos should have size 2
    foos(0) shouldTypeEqual true
    foos(1) shouldTypeEqual false
  }

  it should "fail to compile when field does not exist" in {
    import shapeless.test.illTyped

    illTyped("Pluck.pluck(xs, \"blubb\")",
      ".*Foo does not have a field String\\(\"blubb\"\\)\\.")
  }

  it should "fail to compile when target type does not conform to field type" in {
    import shapeless.test.illTyped

    illTyped("Pluck.pluck(xs, \"foo\"): List[Int]",
      "type mismatch.*found.*List\\[String\\].*required.*List\\[Int\\]")
  }
}
