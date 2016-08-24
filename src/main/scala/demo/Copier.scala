package demo

import shapeless._
import shapeless.ops.record.Merger

import scala.annotation.implicitNotFound


@implicitNotFound("Could not find generic copy signature for ${UserType} and the parameters (as shapeless record) ${Params}")
trait Copier[UserType, Params <: HList] {
  def apply(userType: UserType, params: Params): UserType
}

object Copier extends SealedTraitCopier {
  implicit final class syntax[UserType](private val userType: UserType) {
    object copy extends RecordArgs {
      def applyRecord[Params <: HList](params: Params)(
        implicit
        copier: Copier[UserType, Params]
      ): UserType =
        copier(userType, params)
    }
  }
}

trait SealedTraitCopier extends CaseClassCopier {

  implicit def cnilUpdates[Params <: HList]: Copier[CNil, Params] =
    (a, r) => a

  implicit def cconsUpdates[UserCaseClass, Tail <: Coproduct, Params <: HList](
    implicit
    updateCaseClass: Lazy[Copier[UserCaseClass, Params]],
    updateTail: Lazy[Copier[Tail, Params]]
  ): Copier[UserCaseClass :+: Tail, Params] =
    (a, r) => a match {
      case Inl(h) => Inl(updateCaseClass.value(h, r))
      case Inr(t) => Inr(updateTail.value(t, r))
    }

  implicit def genCoproductUpdates[UserAdt, Params <: HList, ReprOfAdt <: Coproduct](
    implicit
    coprod: HasCoproductGeneric[UserAdt],
    gen: Generic.Aux[UserAdt, ReprOfAdt],
    update: Lazy[Copier[ReprOfAdt, Params]]
  ): Copier[UserAdt, Params] =
    (a, r) => gen.from(update.value(gen.to(a), r))

}

trait CaseClassCopier {

  implicit def mergeUpdates[CaseClass <: HList, Params <: HList](
    implicit merger: Merger.Aux[CaseClass, Params, CaseClass]): Copier[CaseClass, Params] =
    merger(_, _)

  implicit def genProdUpdates[UserCaseClass, Params <: HList, ReprOfCaseClass <: HList](
    implicit
    prod: HasProductGeneric[UserCaseClass],
    gen: LabelledGeneric.Aux[UserCaseClass, ReprOfCaseClass],
    update: Lazy[Copier[ReprOfCaseClass, Params]]
  ): Copier[UserCaseClass, Params] =
    (a, r) => gen.from(update.value(gen.to(a), r))
}
