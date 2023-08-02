package io.fitcentive.awards.domain.metrics

import play.api.libs.json.{Json, OFormat}

import java.time.Instant
import java.util.UUID

case class UserWeightMetrics(
  userId: UUID,
  metricDate: String,
  weightInLbs: Double,
  createdAt: Instant,
  updatedAt: Instant
)

object UserWeightMetrics {
  implicit lazy val format: OFormat[UserWeightMetrics] = Json.format[UserWeightMetrics]
}
