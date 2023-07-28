package io.fitcentive.awards.domain.metrics

import play.api.libs.json.{Json, OFormat}

import java.time.Instant
import java.util.UUID

case class UserStepMetrics(userId: UUID, metricDate: String, stepsTaken: Int, createdAt: Instant, updatedAt: Instant)

object UserStepMetrics {
  implicit lazy val format: OFormat[UserStepMetrics] = Json.format[UserStepMetrics]
}
