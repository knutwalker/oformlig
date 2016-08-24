package demo

import org.scalatest.{ FlatSpec, Matchers }
import shapeless.test.typed

object CopierSpec {
  sealed trait User

  case class RegularUser(name: String, awesome: Boolean) extends User

  case class SpecialUser(awesome: Boolean, name: String, knows: String) extends User

  case class AdminUser(name: String, password: String, awesome: Boolean = true) extends User

  case class SuperUser(reason: String, name: String, awesome: Boolean = false) extends User

//  case class DeletedUser(name: String) extends User

}
final class CopierSpec extends FlatSpec with Matchers {
  import Copier.syntax
  import CopierSpec._

  behavior of "Copier"

  def berndify(user: User): User =
    user.copy(name = "bernd", awesome = true)

  it should "implement copy for RegularUser" in {

    val user: User = RegularUser("edgar", false)
    val bernd = berndify(user)

    typed[User](bernd)
    bernd shouldBe RegularUser("bernd", true)
  }

  it should "implement copy for SpecialUser" in {

    val user: User = SpecialUser(true, "jon", knows = "nothing")
    val bernd = berndify(user)

    typed[User](bernd)
    bernd shouldBe SpecialUser(true, "bernd", "nothing")
  }

  it should "implement copy for AdminUser" in {

    val user: User = AdminUser("root", "ruht")
    val bernd = berndify(user)

    typed[User](bernd)
    bernd shouldBe AdminUser("bernd", "ruht", true)
  }

  it should "implement copy for SuperUser" in {

    val user: User = SuperUser("foo", "ralle")
    val bernd = berndify(user)

    typed[User](bernd)
    bernd shouldBe SuperUser("foo", "bernd", true)
  }
}
