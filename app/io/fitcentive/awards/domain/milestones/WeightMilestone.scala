package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait WeightMilestone extends Milestone {
  override def category: MetricCategory = MetricCategory.WeightData
}

object WeightMilestone {

  def apply(status: String): WeightMilestone =
    status match {
      case ThreeDayStreak.stringValue   => ThreeDayStreak
      case OneWeekStreak.stringValue    => OneWeekStreak
      case TenDayStreak.stringValue     => TenDayStreak
      case TwoWeekStreak.stringValue    => TwoWeekStreak
      case ThreeWeekStreak.stringValue  => ThreeWeekStreak
      case OneMonthStreak.stringValue   => OneMonthStreak
      case TwoMonthStreak.stringValue   => TwoMonthStreak
      case ThreeMonthStreak.stringValue => ThreeMonthStreak
      case SixMonthStreak.stringValue   => SixMonthStreak
      case OneYearStreak.stringValue    => OneYearStreak

      case _ => throw new Exception("Unexpected weight milestone")
    }

  implicit lazy val writes: Writes[WeightMilestone] = {
    {
      case ThreeDayStreak   => JsString(ThreeDayStreak.stringValue)
      case OneWeekStreak    => JsString(OneWeekStreak.stringValue)
      case TenDayStreak     => JsString(TenDayStreak.stringValue)
      case TwoWeekStreak    => JsString(TwoWeekStreak.stringValue)
      case ThreeWeekStreak  => JsString(ThreeWeekStreak.stringValue)
      case OneMonthStreak   => JsString(OneMonthStreak.stringValue)
      case TwoMonthStreak   => JsString(TwoMonthStreak.stringValue)
      case ThreeMonthStreak => JsString(ThreeMonthStreak.stringValue)
      case SixMonthStreak   => JsString(SixMonthStreak.stringValue)
      case OneYearStreak    => JsString(OneYearStreak.stringValue)

    }
  }

  case object ThreeDayStreak extends WeightMilestone {
    val stringValue = "ThreeDayStreak"
  }
  case object OneWeekStreak extends WeightMilestone {
    val stringValue = "OneWeekStreak"
  }
  case object TenDayStreak extends WeightMilestone {
    val stringValue = "TenDayStreak"
  }
  case object TwoWeekStreak extends WeightMilestone {
    val stringValue = "TwoWeekStreak"
  }
  case object ThreeWeekStreak extends WeightMilestone {
    val stringValue = "ThreeWeekStreak"
  }
  case object OneMonthStreak extends WeightMilestone {
    val stringValue = "OneMonthStreak"
  }
  case object TwoMonthStreak extends WeightMilestone {
    val stringValue = "TwoMonthStreak"
  }
  case object ThreeMonthStreak extends WeightMilestone {
    val stringValue = "ThreeMonthStreak"
  }
  case object SixMonthStreak extends WeightMilestone {
    val stringValue = "SixMonthStreak"
  }
  case object OneYearStreak extends WeightMilestone {
    val stringValue = "OneYearStreak"
  }
}
