
import shapeless._
import shapeless.labelled._
import shapeless.ops.record._

import scala.annotation.implicitNotFound

@implicitNotFound("Cannot prove that ${A} has an 'id: Int' field.")
trait HasId[A] {
  def apply(a: A): Int
}

object HasId {
  def apply[A](implicit A: HasId[A]): HasId[A] = A

  def apply[A](a: A)(implicit A: HasId[A]): Int = A(a)

  implicit def hasIdHList[A, R <: HList](
    implicit
    gen: LabelledGeneric.Aux[A, R],
    sel: Selector.Aux[R, Witness.`'id`.T, Int]
  ): HasId[A] = a => sel(gen.to(a))

  implicit def hasIdCoproduct[A, Repr <: Coproduct](
    implicit
    gen: LabelledGeneric.Aux[A, Repr],
    repr: Lazy[HasId[Repr]]
  ): HasId[A] = a => repr.value(gen.to(a))

  implicit val hasIdCNil: HasId[CNil] = a => unexpected

  implicit def hasIdCCons[K <: Symbol, L, R <: Coproduct](
    implicit
    K: Witness.Aux[K],
    L: Lazy[HasId[L]],
    R: Lazy[HasId[R]]
  ): HasId[FieldType[K, L] :+: R] = {
    case Inl(head) ⇒ L.value(head)
    case Inr(tail) ⇒ R.value(tail)
  }
}
