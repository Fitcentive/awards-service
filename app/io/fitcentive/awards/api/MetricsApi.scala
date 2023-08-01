package io.fitcentive.awards.api

import io.fitcentive.awards.api.MetricsApi.{
  activityMilestonesToHourCountMap,
  diaryMilestonesToEntryCountMap,
  stepMilestonesToStepCountMap
}
import io.fitcentive.awards.domain.milestones.{ActivityMilestone, DiaryEntryMilestone, MetricCategory, StepMilestone}
import io.fitcentive.awards.repositories._
import io.fitcentive.awards.services.{MessageBusService, SettingsService}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MetricsApi @Inject() (
  stepMetricsRepository: StepMetricsRepository,
  diaryMetricsRepository: DiaryMetricsRepository,
  userMilestonesRepository: UserMilestonesRepository,
  messageBusService: MessageBusService,
  settingsService: SettingsService
)(implicit ec: ExecutionContext) {

  // We wrap this in a future so that the calling event handler can report success immediately instead of waiting
  def handleUserDiaryEntryCreatedEvent(userId: UUID, date: String, activityMinutes: Option[Int]): Future[Unit] =
    Future {
      for {
        _ <- diaryMetricsRepository.insertUserDiaryDataForDay(userId, date, activityMinutes)
        allTimeDiaryEntries <- diaryMetricsRepository.getUserAllTimeDiaryEntries(userId)
        allTimeActivityMinutes <- diaryMetricsRepository.getUserAllTimeActivityMinutes(userId)

        allDiaryMilestonesAchieved <-
          userMilestonesRepository.getUserMilestonesByCategory(userId, MetricCategory.DiaryEntryData)
        allActivityMilestonesAchieved <-
          userMilestonesRepository.getUserMilestonesByCategory(userId, MetricCategory.ActivityData)

        // Check if any diary milestones can be attained
        diaryMilestoneToBeAttained = {
          if (allDiaryMilestonesAchieved.isEmpty) {
            if (allTimeDiaryEntries > diaryMilestonesToEntryCountMap(DiaryEntryMilestone.TenEntries))
              Some(DiaryEntryMilestone.TenEntries)
            else None
          } else {
            val indexOfMostRecentlyAttainedMilestone =
              DiaryEntryMilestone.diaryEntryMilestonesInOrder.indexOf(allDiaryMilestonesAchieved.last.name)
            if (indexOfMostRecentlyAttainedMilestone == DiaryEntryMilestone.diaryEntryMilestonesInOrder.size - 1)
              None // User attained all milestones
            else {
              val nextPotentialMilestoneToAchieve =
                DiaryEntryMilestone.diaryEntryMilestonesInOrder(indexOfMostRecentlyAttainedMilestone + 1)

              if (allTimeDiaryEntries > diaryMilestonesToEntryCountMap(nextPotentialMilestoneToAchieve))
                Some(nextPotentialMilestoneToAchieve)
              else None
            }

          }
        }
        _ <- diaryMilestoneToBeAttained.fold(Future.unit)(
          newMilestoneToInsert =>
            userMilestonesRepository
              .createUserMilestone(userId, newMilestoneToInsert, MetricCategory.DiaryEntryData)
              .flatMap(messageBusService.publishUserAttainedNewAchievementMilestoneEvent)
        )

        // Check if any activity milestones can be attained
        activityMilestoneToBeAttained = {
          if (allActivityMilestonesAchieved.isEmpty) {
            if (allTimeActivityMinutes > activityMilestonesToHourCountMap(ActivityMilestone.OneHour) * 60)
              Some(ActivityMilestone.OneHour)
            else None
          } else {
            val indexOfMostRecentlyAttainedMilestone =
              ActivityMilestone.activityMilestonesInOrder.indexOf(allActivityMilestonesAchieved.last.name)
            if (indexOfMostRecentlyAttainedMilestone == ActivityMilestone.activityMilestonesInOrder.size - 1)
              None // User attained all milestones
            else {
              val nextPotentialMilestoneToAchieve =
                ActivityMilestone.activityMilestonesInOrder(indexOfMostRecentlyAttainedMilestone + 1)

              if (allTimeActivityMinutes > activityMilestonesToHourCountMap(nextPotentialMilestoneToAchieve) * 60)
                Some(nextPotentialMilestoneToAchieve)
              else None
            }

          }
        }
        _ <- activityMilestoneToBeAttained.fold(Future.unit)(
          newMilestoneToInsert =>
            userMilestonesRepository
              .createUserMilestone(userId, newMilestoneToInsert, MetricCategory.ActivityData)
              .flatMap(messageBusService.publishUserAttainedNewAchievementMilestoneEvent)
        )
      } yield ()
    }

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

  def deleteAllUserData(userId: UUID): Future[Unit] =
    for {
      _ <- Future.unit
      f1 = userMilestonesRepository.deleteAllUserMilestones(userId)
      f2 = stepMetricsRepository.deleteAllStepMetrics(userId)
      f3 = diaryMetricsRepository.deleteAllDiaryMetrics(userId)
      _ <- f1
      _ <- f2
      _ <- f3
    } yield ()
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

  val diaryMilestonesToEntryCountMap: Map[DiaryEntryMilestone, Int] = Map(
    DiaryEntryMilestone.TenEntries -> 10,
    DiaryEntryMilestone.FiftyEntries -> 50,
    DiaryEntryMilestone.HundredEntries -> 100,
    DiaryEntryMilestone.TwoHundredFiftyEntries -> 250,
    DiaryEntryMilestone.FiveHundredEntries -> 500,
    DiaryEntryMilestone.ThousandEntries -> 1000,
    DiaryEntryMilestone.TwoThousandEntries -> 2000,
    DiaryEntryMilestone.FiveThousandEntries -> 5000,
    DiaryEntryMilestone.TenThousandEntries -> 10000,
    DiaryEntryMilestone.TwentyFiveThousandEntries -> 25000,
  )

  val activityMilestonesToHourCountMap: Map[ActivityMilestone, Int] = Map(
    ActivityMilestone.OneHour -> 1,
    ActivityMilestone.TwoHours -> 2,
    ActivityMilestone.FiveHours -> 5,
    ActivityMilestone.TenHours -> 10,
    ActivityMilestone.TwentyFiveHours -> 25,
    ActivityMilestone.FiftyHours -> 50,
    ActivityMilestone.HundredHours -> 100,
    ActivityMilestone.TwoHundredFiftyHours -> 250,
    ActivityMilestone.FiveHundredHours -> 500,
    ActivityMilestone.ThousandHours -> 1000,
  )
}
