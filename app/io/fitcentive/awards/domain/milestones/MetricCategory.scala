package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait MetricCategory {
  def stringValue: String
}

object MetricCategory {

  def apply(status: String): MetricCategory =
    status match {
      case StepData.stringValue => StepData
      case _                    => throw new Exception("Unexpected metric category")
    }

  implicit lazy val writes: Writes[MetricCategory] = {
    {
      case StepData => JsString(StepData.stringValue)
    }
  }

  case object StepData extends MetricCategory {
    val stringValue = "StepData"
  }

}
