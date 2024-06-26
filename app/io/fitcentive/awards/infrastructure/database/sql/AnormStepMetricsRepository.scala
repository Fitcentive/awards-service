package io.fitcentive.awards.infrastructure.database.sql

import anorm.{Macro, RowParser, SqlParser}
import io.fitcentive.awards.domain.metrics.UserStepMetrics
import io.fitcentive.awards.repositories.StepMetricsRepository
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
class AnormStepMetricsRepository @Inject() (val db: Database)(implicit val dbec: DatabaseExecutionContext)
  extends StepMetricsRepository
  with DatabaseClient {

  import AnormStepMetricsRepository._

  override def upsertUserStepDataForDay(userId: UUID, dateString: String, stepsTaken: Int): Future[UserStepMetrics] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[UserStepMetricsRow](
          SQL_UPSERT_AND_RETURN_USER_STEP_METRIC,
          Seq("userId" -> userId, "metricDate" -> dateString, "stepsTaken" -> stepsTaken, "now" -> now)
        )(userStepMetricsRowParser).toDomain
      }
    }

  override def getUserAllTimeStepsTaken(userId: UUID): Future[Int] =
    Future {
      executeSqlWithExpectedReturn(SQL_GET_USER_ALL_TIME_STEPS_TAKEN, Seq("userId" -> userId))(SqlParser.scalar[Int])
    }

  override def deleteAllStepMetrics(userId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(SQL_DELETE_ALL_STEP_METRICS, Seq("userId" -> userId))
    }

  override def getUserStepMetricsForWindow(userId: UUID, from: String, to: String): Future[Seq[UserStepMetrics]] =
    Future {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      getRecords(
        SQL_GET_USER_STEP_METRICS_FOR_WINDOW,
        "userId" -> userId,
        "windowStart" -> sdf.parse(from),
        "windowEnd" -> sdf.parse(to)
      )(userStepMetricsRowParser).map(_.toDomain)
    }

}

object AnormStepMetricsRepository {

  private val SQL_GET_USER_STEP_METRICS_FOR_WINDOW =
    s"""
       |select *
       |from user_step_metrics
       |where user_id = {userId}::uuid
       |and metric_date::date >= {windowStart}
       |and metric_date::date <= {windowEnd} ;
       |""".stripMargin

  private val SQL_DELETE_ALL_STEP_METRICS =
    s"""
       |delete from user_step_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_GET_USER_ALL_TIME_STEPS_TAKEN =
    s"""
       |select sum(steps_taken)
       |from user_step_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_UPSERT_AND_RETURN_USER_STEP_METRIC =
    s"""
       |insert into user_step_metrics (user_id, metric_date, steps_taken, created_at, updated_at)
       |values ({userId}::uuid, {metricDate}, {stepsTaken}, {now}, {now})
       |on conflict (user_id, metric_date)
       |do update set 
       |  steps_taken = {stepsTaken},
       |  updated_at = {now}
       |returning * ;
       |""".stripMargin

  private case class UserStepMetricsRow(
    user_id: UUID,
    metric_date: String,
    steps_taken: Int,
    created_at: Instant,
    updated_at: Instant
  ) {
    def toDomain: UserStepMetrics =
      UserStepMetrics(
        userId = user_id,
        metricDate = metric_date,
        stepsTaken = steps_taken,
        createdAt = created_at,
        updatedAt = updated_at
      )
  }

  private val userStepMetricsRowParser: RowParser[UserStepMetricsRow] = Macro.namedParser[UserStepMetricsRow]

}
