package demo

import org.scalatest.{ FlatSpec, Matchers }
import shapeless.test.illTyped

object HasIdSepc {
  sealed trait CorrectAdt
  sealed trait IncorrectAdt
  case class Foo(id: Int) extends CorrectAdt with IncorrectAdt
  case class Bar(baz: String, id: Int, qux: Int) extends CorrectAdt with IncorrectAdt
  case class Moo() extends IncorrectAdt
  case class Qoo(eiDee: Int)
}
final class HasIdSepc extends FlatSpec with Matchers {
  import HasIdSepc._

  behavior of "HashId"

  it should "find an id field with the type `Int`" in {
    HasId(Foo(42)) shouldBe 42
  }

  it should "find require the name 'id', even in presence of other `Int` fields" in {
    HasId(Bar("foo", 1337, 42)) shouldBe 1337
  }

  it should "fail to compile when no `Int` field is present" in {
    illTyped("HasId[Moo]", "Cannot prove that .*Moo has an 'id: Int' field.")
  }

  it should "fail to compile there is an Int field, but it is not called 'id'" in {
    illTyped("HasId[Qoo]", "Cannot prove that .*Qoo has an 'id: Int' field.")
  }

  it should "support ADTs, when every member has an ID field" in {
    HasId(Foo(42): CorrectAdt) shouldBe 42
    HasId[CorrectAdt](Bar("foo", 1337, 42)) shouldBe 1337
  }

  it should "fail to compile when not every ADT member has an ID field" in {
    illTyped("HasId[IncorrectAdt]", "Cannot prove that .*IncorrectAdt has an 'id: Int' field.")
  }
}
