package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait ActivityMilestone extends Milestone {
  override def category: MetricCategory = MetricCategory.ActivityData
}

object ActivityMilestone {

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

  def apply(status: String): ActivityMilestone =
    status match {
      case OneHour.stringValue              => OneHour
      case TwoHours.stringValue             => TwoHours
      case FiveHours.stringValue            => FiveHours
      case TenHours.stringValue             => TenHours
      case TwentyFiveHours.stringValue      => TwentyFiveHours
      case FiftyHours.stringValue           => FiftyHours
      case HundredHours.stringValue         => HundredHours
      case TwoHundredFiftyHours.stringValue => TwoHundredFiftyHours
      case FiveHundredHours.stringValue     => FiveHundredHours
      case ThousandHours.stringValue        => ThousandHours

      case _ => throw new Exception("Unexpected activity milestone")
    }

  implicit lazy val writes: Writes[ActivityMilestone] = {
    {
      case OneHour              => JsString(OneHour.stringValue)
      case TwoHours             => JsString(TwoHours.stringValue)
      case FiveHours            => JsString(FiveHours.stringValue)
      case TenHours             => JsString(TenHours.stringValue)
      case TwentyFiveHours      => JsString(TwentyFiveHours.stringValue)
      case FiftyHours           => JsString(FiftyHours.stringValue)
      case HundredHours         => JsString(HundredHours.stringValue)
      case TwoHundredFiftyHours => JsString(TwoHundredFiftyHours.stringValue)
      case FiveHundredHours     => JsString(FiveHundredHours.stringValue)
      case ThousandHours        => JsString(ThousandHours.stringValue)

    }
  }

  case object OneHour extends ActivityMilestone {
    val stringValue = "OneHour"
  }
  case object TwoHours extends ActivityMilestone {
    val stringValue = "TwoHours"
  }
  case object FiveHours extends ActivityMilestone {
    val stringValue = "FiveHours"
  }
  case object TenHours extends ActivityMilestone {
    val stringValue = "TenHours"
  }
  case object TwentyFiveHours extends ActivityMilestone {
    val stringValue = "TwentyFiveHours"
  }
  case object FiftyHours extends ActivityMilestone {
    val stringValue = "FiftyHours"
  }
  case object HundredHours extends ActivityMilestone {
    val stringValue = "HundredHours"
  }
  case object TwoHundredFiftyHours extends ActivityMilestone {
    val stringValue = "TwoHundredFiftyHours"
  }
  case object FiveHundredHours extends ActivityMilestone {
    val stringValue = "FiveHundredHours"
  }
  case object ThousandHours extends ActivityMilestone {
    val stringValue = "ThousandHours"
  }
}
