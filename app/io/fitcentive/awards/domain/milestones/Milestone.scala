package io.fitcentive.awards.domain.milestones

import io.fitcentive.awards.domain.milestones.ActivityMilestone.{
  FiftyHours,
  FiveHours,
  FiveHundredHours,
  HundredHours,
  OneHour,
  TenHours,
  ThousandHours,
  TwentyFiveHours,
  TwoHours,
  TwoHundredFiftyHours
}
import io.fitcentive.awards.domain.milestones.DiaryEntryMilestone.{
  FiftyEntries,
  FiveHundredEntries,
  FiveThousandEntries,
  HundredEntries,
  TenEntries,
  TenThousandEntries,
  ThousandEntries,
  TwentyFiveThousandEntries,
  TwoHundredFiftyEntries,
  TwoThousandEntries
}
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

  val activityMilestonesInOrder: Seq[ActivityMilestone] = List(
    OneHour,
    TwoHours,
    FiveHours,
    TenHours,
    TwentyFiveHours,
    FiftyHours,
    HundredHours,
    TwoHundredFiftyHours,
    FiveHundredHours,
    ThousandHours,
  )

  val diaryEntryMilestonesInOrder: Seq[DiaryEntryMilestone] = List(
    TenEntries,
    FiftyEntries,
    HundredEntries,
    TwoHundredFiftyEntries,
    FiveHundredEntries,
    ThousandEntries,
    TwoThousandEntries,
    FiveThousandEntries,
    TenThousandEntries,
    TwentyFiveThousandEntries,
  )

  def apply(status: String): Milestone = {
    if (stepMilestonesInOrder.map(_.stringValue).contains(status)) StepMilestone.apply(status)
    else if (diaryEntryMilestonesInOrder.map(_.stringValue).contains(status)) DiaryEntryMilestone.apply(status)
    else if (activityMilestonesInOrder.map(_.stringValue).contains(status)) ActivityMilestone.apply(status)
    else throw new Exception("Unexpected milestone")
  }

  implicit lazy val writes: Writes[Milestone] = (o: Milestone) => {
    if (stepMilestonesInOrder.map(_.stringValue).contains(o.stringValue)) Json.toJson(o.asInstanceOf[StepMilestone])
    else throw new Exception("Unexpected milestone")
  }
}
