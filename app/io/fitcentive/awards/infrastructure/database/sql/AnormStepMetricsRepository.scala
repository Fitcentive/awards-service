package io.fitcentive.awards.infrastructure.database.sql

import anorm.{Macro, RowParser}
import io.fitcentive.awards.domain.metrics.UserStepMetrics
import io.fitcentive.awards.repositories.StepMetricsRepository
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import io.fitcentive.sdk.infrastructure.database.DatabaseClient
import play.api.db.Database

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
      getRecords(SQL_GET_ALL_USER_STEP_DATA, "userId" -> userId)(userStepMetricsRowParser).map(_.steps_taken).sum
    }
}

object AnormStepMetricsRepository {

  private val SQL_GET_ALL_USER_STEP_DATA =
    s"""
       |select *
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
       |  updated_at = {updatedAt}
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
