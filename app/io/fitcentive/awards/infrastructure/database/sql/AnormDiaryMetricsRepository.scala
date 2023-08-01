package io.fitcentive.awards.infrastructure.database.sql

import anorm.{Macro, RowParser, SqlParser}
import io.fitcentive.awards.domain.metrics.UserDiaryMetrics
import io.fitcentive.awards.repositories.DiaryMetricsRepository
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import io.fitcentive.sdk.infrastructure.database.DatabaseClient
import play.api.db.Database

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.{Date, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class AnormDiaryMetricsRepository @Inject() (val db: Database)(implicit val dbec: DatabaseExecutionContext)
  extends DiaryMetricsRepository
  with DatabaseClient {

  import AnormDiaryMetricsRepository._

  override def insertUserDiaryDataForDay(
    userId: UUID,
    dateString: String,
    activityMinutes: Option[Int]
  ): Future[UserDiaryMetrics] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[UserDiaryMetricsRow](
          SQL_INSERT_AND_RETURN_USER_STEP_METRIC,
          Seq("userId" -> userId, "metricDate" -> dateString, "activityMinutes" -> activityMinutes, "now" -> now)
        )(userDiaryMetricsRowParser).toDomain
      }
    }

  override def getUserAllTimeActivityMinutes(userId: UUID): Future[Int] =
    Future {
      executeSqlWithExpectedReturn(SQL_GET_USER_TOTAL_ACTIVITY_MINUTES_COUNT, Seq("userId" -> userId))(
        SqlParser.scalar[Int]
      )
    }

  override def getUserAllTimeDiaryEntries(userId: UUID): Future[Int] =
    Future {
      executeSqlWithExpectedReturn(SQL_GET_USER_TOTAL_DIARY_ENTRIES_COUNT, Seq("userId" -> userId))(
        SqlParser.scalar[Int]
      )
    }

  override def getUserAllTimeDistinctEntryDates(userId: UUID): Future[Seq[String]] =
    Future {
      getRecords[DistinctMetricDateRow](SQL_GET_USER_ALL_TIME_DISTINCT_ENTRY_DATES, "userId" -> userId)(
        userDistinctMetricDateRowParser
      ).map(_.toDomain)
    }

  override def getUserActivityMinutesForWindow(userId: UUID, windowStart: String, windowEnd: String): Future[Int] = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    Future {
      executeSqlWithExpectedReturn[Int](
        SQL_GET_USER_ACTIVITY_MINUTES_WITHIN_WINDOW,
        Seq(
          "userId" -> userId,
          "windowStartDateString" -> sdf.parse(windowStart),
          "windowEndDateString" -> sdf.parse(windowEnd)
        )
      )(SqlParser.scalar[Int])
    }
  }

  override def deleteAllDiaryMetrics(userId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(SQL_DELETE_ALL_DIARY_METRICS, Seq("userId" -> userId))
    }
}

object AnormDiaryMetricsRepository {
  private val SQL_GET_USER_ACTIVITY_MINUTES_WITHIN_WINDOW: String =
    s"""
       |select coalesce(sum(activity_minutes), 0)
       |from user_diary_metrics
       |where user_id = {userId}::uuid
       |and metric_date::date >= {windowStartDateString}
       |and metric_date::date <= {windowEndDateString} ;
       |""".stripMargin

  private val SQL_GET_USER_ALL_TIME_DISTINCT_ENTRY_DATES: String =
    s"""
       |select distinct metric_date::date 
       |from user_diary_metrics
       |where user_id = {userId}::uuid
       |order by metric_date desc ;
       |""".stripMargin

  private val SQL_DELETE_ALL_DIARY_METRICS =
    s"""
       |delete from user_diary_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_GET_ALL_USER_DIARY_DATA =
    s"""
       |select *
       |from user_diary_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_INSERT_AND_RETURN_USER_STEP_METRIC =
    s"""
       |insert into user_diary_metrics (user_id, metric_date, activity_minutes, created_at, updated_at)
       |values ({userId}::uuid, {metricDate}, {activityMinutes}, {now}, {now})
       |returning * ;
       |""".stripMargin

  private val SQL_GET_USER_TOTAL_ACTIVITY_MINUTES_COUNT =
    s"""
       |select sum(activity_minutes)
       |from user_diary_metrics
       |where user_id = {userId}::uuid
       |""".stripMargin

  private val SQL_GET_USER_TOTAL_DIARY_ENTRIES_COUNT =
    s"""
       |select count(*)
       |from user_diary_metrics
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private case class UserDiaryMetricsRow(
    user_id: UUID,
    metric_date: String,
    activity_minutes: Option[Int],
    created_at: Instant,
    updated_at: Instant
  ) {
    def toDomain: UserDiaryMetrics =
      UserDiaryMetrics(
        userId = user_id,
        metricDate = metric_date,
        activityMinutes = activity_minutes,
        createdAt = created_at,
        updatedAt = updated_at
      )
  }

  private case class DistinctMetricDateRow(metric_date: Date) {
    def toDomain: String = new SimpleDateFormat("MM-dd-yyyy").format(metric_date)
  }

  private val userDiaryMetricsRowParser: RowParser[UserDiaryMetricsRow] = Macro.namedParser[UserDiaryMetricsRow]
  private val userDistinctMetricDateRowParser: RowParser[DistinctMetricDateRow] =
    Macro.namedParser[DistinctMetricDateRow]
}
