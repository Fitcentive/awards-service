package io.fitcentive.awards.api

import io.fitcentive.awards.domain.milestones.{MetricCategory, MilestoneDefinition, UserMilestone}
import io.fitcentive.awards.repositories._
import io.fitcentive.awards.services.{MessageBusService, SettingsService}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AchievementsApi @Inject() (
  stepMetricsRepository: StepMetricsRepository,
  userMilestonesRepository: UserMilestonesRepository,
  messageBusService: MessageBusService,
  settingsService: SettingsService
)(implicit ec: ExecutionContext) {

  def getUserAllTimeAchievementMilestones(
    userId: UUID,
    milestoneCategory: Option[String] = None
  ): Future[Seq[UserMilestone]] =
    milestoneCategory.fold(userMilestonesRepository.getUserMilestones(userId))(
      categoryFilter =>
        userMilestonesRepository.getUserMilestonesByCategory(userId, MetricCategory.apply(categoryFilter))
    )

  def getAllDifferentMilestoneTypes(milestoneCategory: Option[String] = None): Future[Seq[MilestoneDefinition]] =
    milestoneCategory.fold(userMilestonesRepository.getMilestoneTypes)(
      categoryFilter => userMilestonesRepository.getMilestoneTypesByCategory(MetricCategory.apply(categoryFilter))
    )

}
