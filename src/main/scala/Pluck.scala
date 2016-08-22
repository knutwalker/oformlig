
import shapeless._
import shapeless.labelled._
import shapeless.tag._

import scala.annotation.implicitNotFound

object Pluck {

  trait FindByName[L <: HList, K] extends DepFn1[L] {
    type Out
  }

  object FindByName {

    @implicitNotFound("${A} does not have a field ${F}.")
    type PrettyAux[A, F, L <: HList, K, Out0] = FindByName[L, K] {type Out = Out0}
    type Aux[L <: HList, K, Out0] = FindByName[L, K] {type Out = Out0}

    def apply[L <: HList, K](implicit fbn: FindByName[L, K]): Aux[L, K, fbn.Out] = fbn

    implicit def headByName[T <: HList, K, V]
    : Aux[FieldType[K, V] :: T, K, V] =
      new FindByName[FieldType[K, V] :: T, K] {
        type Out = V
        def apply(l: FieldType[K, V] :: T): Out = l.head
      }

    implicit def tailByName[H, T <: HList, K, V](
      implicit
      fbn: FindByName[T, K]
    ): Aux[H :: T, K, fbn.Out] =
      new FindByName[H :: T, K] {
        type Out = fbn.Out
        def apply(l: H :: T): Out = fbn(l.tail)
      }
  }

  def pluck[A, T, R <: HList](col: List[A], item: String)(
    implicit
    gen: LabelledGeneric.Aux[A, R],
    bn: FindByName.PrettyAux[A, item.type, R, Symbol @@ item.type, T]
  ): List[T] = col.map(x => bn(gen.to(x)))
}
