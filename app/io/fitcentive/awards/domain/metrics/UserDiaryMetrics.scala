package io.fitcentive.awards.domain.metrics

import play.api.libs.json.{Json, OFormat}

import java.time.Instant
import java.util.UUID

case class UserDiaryMetrics(
  userId: UUID,
  metricDate: String,
  activityMinutes: Option[Int],
  createdAt: Instant,
  updatedAt: Instant
)

object UserDiaryMetrics {
  implicit lazy val format: OFormat[UserDiaryMetrics] = Json.format[UserDiaryMetrics]
}
