package io.fitcentive.awards.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.milestones.{MetricCategory, Milestone, MilestoneDefinition, UserMilestone}
import io.fitcentive.awards.infrastructure.database.sql.AnormUserMilestonesRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormUserMilestonesRepository])
trait UserMilestonesRepository {
  def createUserMilestone(
    userId: UUID,
    milestoneName: Milestone,
    milestoneCategory: MetricCategory
  ): Future[UserMilestone]
  def getUserMilestonesByCategory(userId: UUID, category: MetricCategory): Future[Seq[UserMilestone]]
  def getUserMilestones(userId: UUID): Future[Seq[UserMilestone]]
  def getMilestoneTypes: Future[Seq[MilestoneDefinition]]
  def getMilestoneTypesByCategory(category: MetricCategory): Future[Seq[MilestoneDefinition]]
}
