package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait Categories {
  def stringValue: String
}

object Categories {

  def apply(status: String): Categories =
    status match {
      case StepData.stringValue => StepData
      case _                    => throw new Exception("Unexpected step milestone")
    }

  implicit lazy val writes: Writes[Categories] = {
    {
      case StepData => JsString(StepData.stringValue)
    }
  }

  case object StepData extends Categories {
    val stringValue = "StepData"
  }

}
