package io.fitcentive.awards.api

import io.fitcentive.awards.api.MetricsApi.stepMilestonesToStepCountMap

import io.fitcentive.awards.domain.milestones.{MetricCategory, StepMilestone}
import io.fitcentive.awards.repositories._
import io.fitcentive.awards.services.{MessageBusService, SettingsService}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MetricsApi @Inject() (
  stepMetricsRepository: StepMetricsRepository,
  userMilestonesRepository: UserMilestonesRepository,
  messageBusService: MessageBusService,
  settingsService: SettingsService
)(implicit ec: ExecutionContext) {

  // We wrap this in a future so that the calling event handler can report success immediately instead of waiting
  def handleUserStepDataUpdatedEvent(userId: UUID, date: String, stepsTaken: Int): Future[Unit] =
    Future {
      for {
        _ <- stepMetricsRepository.upsertUserStepDataForDay(userId, date, stepsTaken)
        allTimeSteps <- stepMetricsRepository.getUserAllTimeStepsTaken(userId)
        allStepMilestonesAttained <-
          userMilestonesRepository.getUserMilestonesByCategory(userId, MetricCategory.StepData)

        milestoneToBeAttained = {
          if (allStepMilestonesAttained.isEmpty) {
            if (allTimeSteps > stepMilestonesToStepCountMap(StepMilestone.TenThousandSteps))
              Some(StepMilestone.TenThousandSteps)
            else None
          } else {
            val indexOfMostRecentlyAttainedMilestone =
              StepMilestone.stepMilestonesInOrder.indexOf(allStepMilestonesAttained.last.name)
            if (indexOfMostRecentlyAttainedMilestone == StepMilestone.stepMilestonesInOrder.size - 1)
              None // User attained all milestones
            else {
              val nextPotentialMilestoneToAchieve =
                StepMilestone.stepMilestonesInOrder(indexOfMostRecentlyAttainedMilestone + 1)

              if (allTimeSteps > stepMilestonesToStepCountMap(nextPotentialMilestoneToAchieve))
                Some(nextPotentialMilestoneToAchieve)
              else None
            }

          }
        }

        _ <- milestoneToBeAttained.fold(Future.unit)(
          newMilestoneToInsert =>
            userMilestonesRepository
              .createUserMilestone(userId, newMilestoneToInsert, MetricCategory.StepData)
              .flatMap(messageBusService.publishUserAttainedNewAchievementMilestoneEvent)
        )

      } yield ()
    }
}

object MetricsApi {
  val stepMilestonesToStepCountMap: Map[StepMilestone, Int] = Map(
    StepMilestone.TenThousandSteps -> 10000,
    StepMilestone.FiftyThousandSteps -> 50000,
    StepMilestone.HundredThousandSteps -> 100000,
    StepMilestone.TwoFiftyThousandSteps -> 250000,
    StepMilestone.FiveHundredThousandSteps -> 500000,
    StepMilestone.OneMillionStepsSteps -> 1000000,
    StepMilestone.TwoMillionStepsSteps -> 2000000,
    StepMilestone.FiveMillionStepsSteps -> 5000000,
    StepMilestone.TenMillionStepsSteps -> 10000000,
  )
}
