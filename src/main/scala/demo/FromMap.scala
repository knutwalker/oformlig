package demo

import validation.Result
import validation.Result.fromOption

import shapeless._
import shapeless.labelled._


sealed trait Error

case class Missing(key: String) extends Error
case class WrongType(key: String, expected: String) extends Error
case class Mistyped[A](key: String, value: Any)(implicit val expexted: Typeable[A]) extends Error


trait FromMap[R <: HList] extends ((Map[String, Any], Option[String]) => Result[Error, R]) {
  def apply(m: Map[String, Any], parent: Option[String]): Result[Error, R]
}

trait LowPriorityFromMap0 {
  implicit def hconsFromMap0[K <: Symbol, V, T <: HList](
    implicit
    K: Witness.Aux[K],
    V: Typeable[V],
    T: Lazy[FromMap[T]]
  ): FromMap[FieldType[K, V] :: T] = (m, p) => {
    val name = K.value.name
    val fullName = p.fold(name)(_ + s".$name")
    val value = for {
      v <- fromOption(m.get(name), Missing(fullName))
      x <- fromOption(V.cast(v), Mistyped[V](fullName, v))
    } yield x
    val tail = T.value(m, p)
    (value and tail) ((v, t) => field[K](v) :: t)
  }
}

trait LowPriorityFromMap1 extends LowPriorityFromMap0 {
  implicit def hconsFromMap1[K <: Symbol, V, R <: HList, T <: HList](
    implicit
    K: Witness.Aux[K],
    V: LabelledGeneric.Aux[V, R],
    R: Lazy[FromMap[R]],
    T: Lazy[FromMap[T]]
  ): FromMap[FieldType[K, V] :: T] = (m, p) => {
    val name = K.value.name
    val fullName = p.fold(name)(_ + s".$name")
    val value = for {
      k <- fromOption(m.get(name), Missing(fullName))
      v <- fromOption(Typeable[Map[String, Any]].cast(k), WrongType(fullName, "nested map"))
      x <- R.value(v, Some(fullName))
    } yield x
    val tail = T.value(m, p)
    (value and tail) ((r, t) => field[K](V.from(r)) :: t)
  }
}

object FromMap extends LowPriorityFromMap1 {
  private[this] val some_hnil                  = Result.valid(HNil)
  implicit      val hnilFromMap: FromMap[HNil] = (m, p) => some_hnil

  implicit def hconsFromMap2[K <: Symbol, V, R <: HList, T <: HList](
    implicit
    K: Witness.Aux[K],
    V: LabelledGeneric.Aux[V, R],
    R: Lazy[FromMap[R]],
    T: Lazy[FromMap[T]]
  ): FromMap[FieldType[K, List[V]] :: T] = (m, p) => {
    val name = K.value.name
    val fullName = p.fold(name)(_ + s".$name")

    val value = for {
      k <- fromOption(m.get(name), Missing(fullName))
      xs <- fromOption(Typeable[List[Map[String, Any]]].cast(k), WrongType(fullName, "nested list of maps"))
      x <- Result.traverse(xs.zipWithIndex) {case (v, i) => R.value(v, Some(s"$fullName.$i"))}
    } yield x

    val tail = T.value(m, p)
    (value and tail) ((r, t) => field[K](r.map(V.from)) :: t)
  }

  class FromMapper[A] {
    def apply[R <: HList](m: Map[String, Any])(
      implicit
      A: LabelledGeneric.Aux[A, R],
      R: FromMap[R]
    ): Result[Error, A] =
      R(m, None).map(A.from)
  }

  def to[A](implicit A: LabelledGeneric[A]) = new FromMapper[A]
}
