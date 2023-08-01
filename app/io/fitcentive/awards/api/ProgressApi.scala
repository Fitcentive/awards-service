package io.fitcentive.awards.api

import io.fitcentive.awards.domain.progress.ProgressInsights
import io.fitcentive.awards.repositories._
import io.fitcentive.awards.services.{MessageBusService, SettingsService}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class ProgressApi @Inject() (
  stepMetricsRepository: StepMetricsRepository,
  diaryMetricsRepository: DiaryMetricsRepository,
  userMilestonesRepository: UserMilestonesRepository,
  messageBusService: MessageBusService,
  settingsService: SettingsService
)(implicit ec: ExecutionContext) {

  private def calculateDiaryEntryStreak(distinctDateStrings: Seq[String], offsetInMinutes: Int): Int = {
    def calculateInternal(currentIndex: Int): Int = {
      val currentDay = LocalDateTime
        .ofInstant(Instant.now(), ZoneOffset.UTC)
        .plus(-offsetInMinutes, ChronoUnit.MINUTES)
        .minus(currentIndex, ChronoUnit.DAYS)
        .format(DateTimeFormatter.ISO_LOCAL_DATE)

      if (distinctDateStrings(currentIndex) == currentDay) 1 + calculateInternal(currentIndex + 1)
      else 0
    }
    calculateInternal(0)
  }

  def getUserProgressInsights(userId: UUID, offsetInMinutes: Int): Future[ProgressInsights] =
    for {
      distinctDiaryEntryDates <- diaryMetricsRepository.getUserAllTimeDistinctEntryDates(userId)
      currentDiaryEntryStreak = calculateDiaryEntryStreak(distinctDiaryEntryDates, offsetInMinutes)

      now = Instant.now()
      windowEnd =
        LocalDateTime
          .ofInstant(now, ZoneOffset.UTC)
          .plus(-offsetInMinutes, ChronoUnit.MINUTES)
          .format(DateTimeFormatter.ISO_LOCAL_DATE)
      windowStart =
        LocalDateTime
          .ofInstant(now, ZoneOffset.UTC)
          .plus(-offsetInMinutes, ChronoUnit.MINUTES)
          .minus(7, ChronoUnit.DAYS) // Get past 7 days for now. Future improvement to use week blocks instead
          .format(DateTimeFormatter.ISO_LOCAL_DATE)
      activityMinutesForTheWeek <-
        diaryMetricsRepository.getUserActivityMinutesForWindow(userId, windowStart, windowEnd)
    } yield ProgressInsights(currentDiaryEntryStreak, activityMinutesForTheWeek)

}
