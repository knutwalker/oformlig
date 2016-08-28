package demo

import shapeless._
import shapeless.labelled._
import shapeless.tag._

import scala.annotation.implicitNotFound


trait Pluck[L <: HList, K] {
  type Out
  def apply(l: L): Out
}

object Pluck {
  type Aux[L <: HList, K, Out0] = Pluck[L, K] {type Out = Out0}

  @implicitNotFound("${A} does not have a field ${F}.")
  type PrettyAux[A, F, L <: HList, K, Out0] = Pluck[L, K] {type Out = Out0}

  def apply[A, Field <: Singleton, Repr <: HList, Out](xs: List[A], field: Field)(
    implicit
    gen: LabelledGeneric.Aux[A, Repr],
    pluck: Pluck.PrettyAux[A, Field, Repr, Symbol @@ Field, Out]
  ): List[Out] = xs.map(x => pluck(gen.to(x)))

  implicit def headByName[T <: HList, K, V]: Aux[FieldType[K, V] :: T, K, V] =
    new Pluck[FieldType[K, V] :: T, K] {
      type Out = V
      def apply(l: FieldType[K, V] :: T): Out = l.head
    }

  implicit def tailByName[H, T <: HList, K](
    implicit
    fbn: Pluck[T, K]
  ): Aux[H :: T, K, fbn.Out] =
    new Pluck[H :: T, K] {
      type Out = fbn.Out
      def apply(l: H :: T): Out = fbn(l.tail)
    }

  implicit final class syntax[A](private val xs: List[A]) extends AnyVal {
    def pluck[Field <: Singleton, Repr <: HList, Out](field: Field)(
      implicit
      gen: LabelledGeneric.Aux[A, Repr],
      pluck: Pluck.PrettyAux[A, Field, Repr, Symbol @@ Field, Out]
    ): List[Out] = apply[A, Field, Repr, Out](xs, field)
  }
}
