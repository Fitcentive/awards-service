package io.fitcentive.awards.infrastructure.database.sql

import anorm.{Macro, RowParser, SqlParser}
import io.fitcentive.awards.domain.metrics.UserWeightMetrics
import io.fitcentive.awards.repositories.WeightMetricsRepository
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import io.fitcentive.sdk.infrastructure.database.DatabaseClient
import play.api.db.Database

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class AnormWeightMetricsRepository @Inject() (val db: Database)(implicit val dbec: DatabaseExecutionContext)
  extends WeightMetricsRepository
  with DatabaseClient {

  import AnormWeightMetricsRepository._

  override def upsertUserWeightDataForDay(
    userId: UUID,
    dateString: String,
    weightInLbs: Double
  ): Future[UserWeightMetrics] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[UserWeightMetricsRow](
          SQL_UPSERT_AND_RETURN_USER_WEIGHT_METRIC,
          Seq("userId" -> userId, "metricDate" -> dateString, "weightInLbs" -> weightInLbs, "now" -> now)
        )(userUserWeightMetricsRowParser).toDomain
      }
    }

  override def getUserAllTimeWeightEntries(userId: UUID): Future[Seq[UserWeightMetrics]] =
    Future {
      getRecords(SQL_GET_ALL_TIME_USER_WEIGHT_METRICS, "userId" -> userId)(userUserWeightMetricsRowParser)
        .map(_.toDomain)
    }

  override def deleteAllWeightMetrics(userId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(SQL_DELETE_ALL_WEIGHT_METRICS, Seq("userId" -> userId))
    }

  override def getUserWeightMetricsForWindow(userId: UUID, from: String, to: String): Future[Seq[UserWeightMetrics]] =
    Future {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      getRecords(
        SQL_GET_USER_WEIGHT_METRICS_FOR_WINDOW,
        "userId" -> userId,
        "windowStart" -> sdf.parse(from),
        "windowEnd" -> sdf.parse(to)
      )(userUserWeightMetricsRowParser).map(_.toDomain)
    }

}

object AnormWeightMetricsRepository {

  private val SQL_GET_ALL_TIME_USER_WEIGHT_METRICS =
    s"""
       |select *
       |from user_weight_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_GET_USER_WEIGHT_METRICS_FOR_WINDOW =
    s"""
       |select *
       |from user_weight_metrics
       |where user_id = {userId}::uuid
       |and metric_date::date >= {windowStart}
       |and metric_date::date <= {windowEnd} ;
       |""".stripMargin

  private val SQL_DELETE_ALL_WEIGHT_METRICS =
    s"""
       |delete from user_weight_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_GET_USER_ALL_TIME_WEIGHT_ENTRIES =
    s"""
       |select count(*)
       |from user_weight_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_UPSERT_AND_RETURN_USER_WEIGHT_METRIC =
    s"""
       |insert into user_weight_metrics (user_id, metric_date, weight_in_lbs, created_at, updated_at)
       |values ({userId}::uuid, {metricDate}, {weightInLbs}, {now}, {now})
       |on conflict (user_id, metric_date)
       |do update set 
       |  weight_in_lbs = {weightInLbs},
       |  updated_at = {now}
       |returning * ;
       |""".stripMargin

  private case class UserWeightMetricsRow(
    user_id: UUID,
    metric_date: String,
    weight_in_lbs: Double,
    created_at: Instant,
    updated_at: Instant
  ) {
    def toDomain: UserWeightMetrics =
      UserWeightMetrics(
        userId = user_id,
        metricDate = metric_date,
        weightInLbs = weight_in_lbs,
        createdAt = created_at,
        updatedAt = updated_at
      )
  }

  private val userUserWeightMetricsRowParser: RowParser[UserWeightMetricsRow] = Macro.namedParser[UserWeightMetricsRow]

}
