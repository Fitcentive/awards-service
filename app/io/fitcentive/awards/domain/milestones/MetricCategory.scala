package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait MetricCategory {
  def stringValue: String
}

object MetricCategory {

  def apply(status: String): MetricCategory =
    status match {
      case StepData.stringValue       => StepData
      case DiaryEntryData.stringValue => DiaryEntryData
      case ActivityData.stringValue   => ActivityData
      case WeightData.stringValue     => WeightData
      case _                          => throw new Exception("Unexpected metric category")
    }

  implicit lazy val writes: Writes[MetricCategory] = {
    {
      case StepData       => JsString(StepData.stringValue)
      case DiaryEntryData => JsString(DiaryEntryData.stringValue)
      case ActivityData   => JsString(ActivityData.stringValue)
      case WeightData     => JsString(WeightData.stringValue)
    }
  }

  case object StepData extends MetricCategory {
    val stringValue = "StepData"
  }

  case object DiaryEntryData extends MetricCategory {
    val stringValue = "DiaryEntryData"
  }

  case object ActivityData extends MetricCategory {
    val stringValue = "ActivityData"
  }

  case object WeightData extends MetricCategory {
    val stringValue = "WeightData"
  }

}
