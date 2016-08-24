package demo

import org.scalatest.{ FlatSpec, Matchers }
import shapeless.test._

object OldSchoolCopierSpec {

  sealed trait User {
    def name: String
    def awesome: Boolean

    def makeCopy(name: String = name, awesome: Boolean = awesome): User
  }

  case class RegularUser(name: String, awesome: Boolean) extends User {
    def makeCopy(name: String, awesome: Boolean): User =
      copy(name, awesome)
  }

  case class SpecialUser(awesome: Boolean, name: String, knows: String) extends User {
    def makeCopy(name: String, awesome: Boolean): User =
      copy(awesome, name, knows)
  }

  case class AdminUser(name: String, password: String, awesome: Boolean = true) extends User {
    def makeCopy(name: String, awesome: Boolean): User =
      copy(name, password, awesome)
  }

  case class SuperUser(reason: String, name: String, awesome: Boolean = false) extends User {
    def makeCopy(name: String, awesome: Boolean): User =
      copy(reason, name, awesome)
  }
}
final class OldSchoolCopierSpec extends FlatSpec with Matchers {
  import OldSchoolCopierSpec._

  behavior of "OldCopier"

  def berndify(user: User): User =
    user.makeCopy(name = "bernd", awesome = true)

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
