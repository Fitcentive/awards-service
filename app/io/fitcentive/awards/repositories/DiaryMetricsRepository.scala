package io.fitcentive.awards.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.metrics.UserDiaryMetrics
import io.fitcentive.awards.domain.progress.{ActivityMinutesPerDay, DiaryEntryCountPerDay}
import io.fitcentive.awards.infrastructure.database.sql.AnormDiaryMetricsRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormDiaryMetricsRepository])
trait DiaryMetricsRepository {
  def insertUserDiaryDataForDay(
    userId: UUID,
    dateString: String,
    activityMinutes: Option[Int]
  ): Future[UserDiaryMetrics]
  def getUserAllTimeActivityMinutes(userId: UUID): Future[Int]
  def getUserAllTimeDiaryEntries(userId: UUID): Future[Int]
  def getUserAllTimeDistinctEntryDates(userId: UUID): Future[Seq[String]]
  def getUserActivityMinutesForWindow(userId: UUID, windowStart: String, windowEnd: String): Future[Int]
  def deleteAllDiaryMetrics(userId: UUID): Future[Unit]
  def getUserActivityProgressMetrics(
    userId: UUID,
    windowStart: String,
    windowEnd: String
  ): Future[Seq[ActivityMinutesPerDay]]
  def getUserDiaryEntryCountPerDayByWindow(
    userId: UUID,
    windowStart: String,
    windowEnd: String
  ): Future[Seq[DiaryEntryCountPerDay]]
}
