package io.fitcentive.awards.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.metrics.UserStepMetrics
import io.fitcentive.awards.infrastructure.database.sql.AnormStepMetricsRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormStepMetricsRepository])
trait StepMetricsRepository {
  def upsertUserStepDataForDay(userId: UUID, dateString: String, stepsTaken: Int): Future[UserStepMetrics]
  def getUserAllTimeStepsTaken(userId: UUID): Future[Int]
  def deleteAllStepMetrics(userId: UUID): Future[Unit]
}
