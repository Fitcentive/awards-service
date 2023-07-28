package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait StepMilestone extends Milestone {
  override def category: MetricCategory = MetricCategory.StepData
}

object StepMilestone {

  val stepMilestonesInOrder: Seq[StepMilestone] = List(
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

  def apply(status: String): StepMilestone =
    status match {
      case TenThousandSteps.stringValue         => TenThousandSteps
      case FiftyThousandSteps.stringValue       => FiftyThousandSteps
      case HundredThousandSteps.stringValue     => HundredThousandSteps
      case TwoFiftyThousandSteps.stringValue    => TwoFiftyThousandSteps
      case FiveHundredThousandSteps.stringValue => FiveHundredThousandSteps
      case OneMillionStepsSteps.stringValue     => OneMillionStepsSteps
      case TwoMillionStepsSteps.stringValue     => TwoMillionStepsSteps
      case FiveMillionStepsSteps.stringValue    => FiveMillionStepsSteps
      case TenMillionStepsSteps.stringValue     => TenMillionStepsSteps
      case _                                    => throw new Exception("Unexpected step milestone")
    }

  implicit lazy val writes: Writes[StepMilestone] = {
    {
      case TenThousandSteps         => JsString(TenThousandSteps.stringValue)
      case FiftyThousandSteps       => JsString(FiftyThousandSteps.stringValue)
      case HundredThousandSteps     => JsString(HundredThousandSteps.stringValue)
      case TwoFiftyThousandSteps    => JsString(TwoFiftyThousandSteps.stringValue)
      case FiveHundredThousandSteps => JsString(FiveHundredThousandSteps.stringValue)
      case OneMillionStepsSteps     => JsString(OneMillionStepsSteps.stringValue)
      case TwoMillionStepsSteps     => JsString(TwoMillionStepsSteps.stringValue)
      case FiveMillionStepsSteps    => JsString(FiveMillionStepsSteps.stringValue)
      case TenMillionStepsSteps     => JsString(TenMillionStepsSteps.stringValue)
    }
  }

  case object TenThousandSteps extends StepMilestone {
    val stringValue = "TenThousandSteps"
  }
  case object FiftyThousandSteps extends StepMilestone {
    val stringValue = "FiftyThousandSteps"
  }
  case object HundredThousandSteps extends StepMilestone {
    val stringValue = "HundredThousandSteps"
  }
  case object TwoFiftyThousandSteps extends StepMilestone {
    val stringValue = "TwoFiftyThousandSteps"
  }
  case object FiveHundredThousandSteps extends StepMilestone {
    val stringValue = "FiveHundredThousandSteps"
  }
  case object OneMillionStepsSteps extends StepMilestone {
    val stringValue = "OneMillionStepsSteps"
  }
  case object TwoMillionStepsSteps extends StepMilestone {
    val stringValue = "TwoMillionStepsSteps"
  }
  case object FiveMillionStepsSteps extends StepMilestone {
    val stringValue = "FiveMillionStepsSteps"
  }
  case object TenMillionStepsSteps extends StepMilestone {
    val stringValue = "TenMillionStepsSteps"
  }
}
