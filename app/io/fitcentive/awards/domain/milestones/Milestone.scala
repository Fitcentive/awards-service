package io.fitcentive.awards.domain.milestones

import io.fitcentive.awards.domain.milestones.StepMilestone._
import play.api.libs.json.{Json, Writes}

trait Milestone {
  def stringValue: String
  def category: MetricCategory
}

object Milestone {

  val stepMilestonesInOrder = List(
    TenThousandSteps,
    FiftyThousandSteps,
    HundredThousandSteps,
    TwoFiftyThousandSteps,
    FiveHundredThousandSteps,
    OneMillionStepsSteps,
    TwoMillionStepsSteps,
    FiveMillionStepsSteps,
    TenMillionStepsSteps,
  )

  def apply(status: String): Milestone = {
    if (stepMilestonesInOrder.map(_.stringValue).contains(status)) StepMilestone.apply(status)
    else throw new Exception("Unexpected milestone")
  }

  implicit lazy val writes: Writes[Milestone] = (o: Milestone) => {
    if (stepMilestonesInOrder.map(_.stringValue).contains(o.stringValue)) Json.toJson(o.asInstanceOf[StepMilestone])
    else throw new Exception("Unexpected milestone")
  }
}
