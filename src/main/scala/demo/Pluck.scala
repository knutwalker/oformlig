package demo

import cats.Functor

import shapeless._
import shapeless.labelled._
import shapeless.tag._

import scala.annotation.implicitNotFound


trait Pluck[Repr <: HList, Field] {
  type Out
  def apply(l: Repr): Out
}

object Pluck {
  type Aux[Repr <: HList, Field, Out0] = Pluck[Repr, Field] {type Out = Out0}

  @implicitNotFound("${Type} does not have a field ${FieldName}.")
  type PrettyAux[Type, FieldName, Repr <: HList, Field, Out0] = Pluck[Repr, Field] {type Out = Out0}

  def apply[F[_], Type, Field <: Singleton, Repr <: HList, Out](xs: F[Type], field: Field)(
    implicit
    functor: Functor[F],
    gen: LabelledGeneric.Aux[Type, Repr],
    pluck: Pluck.PrettyAux[Type, Field, Repr, Symbol @@ Field, Out]
  ): F[Out] = functor.map(xs)(x => pluck(gen.to(x)))

  implicit def pluckHead[Tail <: HList, Field, Out0]: Aux[FieldType[Field, Out0] :: Tail, Field, Out0] =
    new Pluck[FieldType[Field, Out0] :: Tail, Field] {
      type Out = Out0
      def apply(l: FieldType[Field, Out0] :: Tail): Out = l.head
    }

  implicit def pluckTail[Head, Tail <: HList, Field](
    implicit
    pluck: Pluck[Tail, Field]
  ): Aux[Head :: Tail, Field, pluck.Out] =
    new Pluck[Head :: Tail, Field] {
      type Out = pluck.Out
      def apply(l: Head :: Tail): Out = pluck(l.tail)
    }

  implicit final class syntax[F[_], A](private val xs: F[A]) extends AnyVal {
    def pluck[Field <: Singleton, Repr <: HList, Out](field: Field)(
      implicit
      functor: Functor[F],
      gen: LabelledGeneric.Aux[A, Repr],
      pluck: Pluck.PrettyAux[A, Field, Repr, Symbol @@ Field, Out]
    ): F[Out] = apply[F, A, Field, Repr, Out](xs, field)
  }
}
