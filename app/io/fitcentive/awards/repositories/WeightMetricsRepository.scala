package io.fitcentive.awards.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.metrics.UserWeightMetrics
import io.fitcentive.awards.infrastructure.database.sql.AnormWeightMetricsRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormWeightMetricsRepository])
trait WeightMetricsRepository {
  def upsertUserWeightDataForDay(userId: UUID, dateString: String, weightInLbs: Double): Future[UserWeightMetrics]
  def getUserAllTimeWeightEntries(userId: UUID): Future[Seq[UserWeightMetrics]]
  def deleteAllWeightMetrics(userId: UUID): Future[Unit]
  def getUserWeightMetricsForWindow(userId: UUID, from: String, to: String): Future[Seq[UserWeightMetrics]]
}
