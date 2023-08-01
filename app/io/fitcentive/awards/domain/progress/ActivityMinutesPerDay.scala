package io.fitcentive.awards.domain.progress

import play.api.libs.json.{Json, Writes}

case class ActivityMinutesPerDay(metricDate: String, activityMinutes: Int)

object ActivityMinutesPerDay {
  implicit lazy val writes: Writes[ActivityMinutesPerDay] = Json.writes[ActivityMinutesPerDay]
}
