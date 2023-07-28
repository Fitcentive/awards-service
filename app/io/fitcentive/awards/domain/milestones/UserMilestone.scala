package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{Json, Writes}

import java.time.Instant
import java.util.UUID

case class UserMilestone(
  userId: UUID,
  name: Milestone,
  milestoneCategory: MetricCategory,
  createdAt: Instant,
  updatedAt: Instant
)

object UserMilestone {
  implicit lazy val writes: Writes[UserMilestone] = Json.writes[UserMilestone]
}
