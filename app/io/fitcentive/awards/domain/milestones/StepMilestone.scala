package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait Milestone {
  def stringValue: String
}

object Milestone {
  def apply(status: String): Milestone =
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
      case _                                    => throw new Exception("Unexpected milestone")
    }

  implicit lazy val writes: Writes[Milestone] = {
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

  case object TenThousandSteps extends Milestone {
    val stringValue = "TenThousandSteps"
  }
  case object FiftyThousandSteps extends Milestone {
    val stringValue = "FiftyThousandSteps"
  }
  case object HundredThousandSteps extends Milestone {
    val stringValue = "HundredThousandSteps"
  }
  case object TwoFiftyThousandSteps extends Milestone {
    val stringValue = "TwoFiftyThousandSteps"
  }
  case object FiveHundredThousandSteps extends Milestone {
    val stringValue = "FiveHundredThousandSteps"
  }
  case object OneMillionStepsSteps extends Milestone {
    val stringValue = "OneMillionStepsSteps"
  }
  case object TwoMillionStepsSteps extends Milestone {
    val stringValue = "TwoMillionStepsSteps"
  }
  case object FiveMillionStepsSteps extends Milestone {
    val stringValue = "FiveMillionStepsSteps"
  }
  case object TenMillionStepsSteps extends Milestone {
    val stringValue = "TenMillionStepsSteps"
  }
}
