package io.fitcentive.awards.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.metrics.UserDiaryMetrics
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
  def deleteAllDiaryMetrics(userId: UUID): Future[Unit]
}
