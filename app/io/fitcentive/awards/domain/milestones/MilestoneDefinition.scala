package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{Json, Writes}

case class MilestoneDefinition(name: Milestone, category: MetricCategory, description: String)

object MilestoneDefinition {
  implicit lazy val writes: Writes[MilestoneDefinition] = Json.writes[MilestoneDefinition]
}
