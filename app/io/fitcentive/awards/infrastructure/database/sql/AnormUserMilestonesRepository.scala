package io.fitcentive.awards.infrastructure.database.sql

import anorm.{Macro, RowParser}
import io.fitcentive.awards.domain.milestones.{MetricCategory, Milestone, MilestoneDefinition, UserMilestone}
import io.fitcentive.awards.repositories.UserMilestonesRepository
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import io.fitcentive.sdk.infrastructure.database.DatabaseClient
import play.api.db.Database

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class AnormUserMilestonesRepository @Inject() (val db: Database)(implicit val dbec: DatabaseExecutionContext)
  extends UserMilestonesRepository
  with DatabaseClient {

  import AnormUserMilestonesRepository._

  override def createUserMilestone(
    userId: UUID,
    milestoneName: Milestone,
    milestoneCategory: MetricCategory
  ): Future[UserMilestone] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[UserMilestonesRow](
          SQL_CREATE_AND_RETURN_NEW_USER_MILESTONE,
          Seq(
            "userId" -> userId,
            "milestoneName" -> milestoneName.stringValue,
            "milestoneCategory" -> milestoneCategory.stringValue,
            "now" -> now
          )
        )(userMilestonesRowParser).toDomain
      }
    }

  override def getUserMilestonesByCategory(userId: UUID, category: MetricCategory): Future[Seq[UserMilestone]] =
    Future {
      getRecords(
        SQL_GET_ALL_USER_MILESTONES_BY_CATEGORY,
        "userId" -> userId,
        "milestoneCategory" -> category.stringValue
      )(userMilestonesRowParser).map(_.toDomain)
    }

  override def getUserMilestones(userId: UUID): Future[Seq[UserMilestone]] =
    Future {
      getRecords(SQL_GET_ALL_USER_MILESTONES, "userId" -> userId)(userMilestonesRowParser).map(_.toDomain)
    }

  override def getMilestoneTypes: Future[Seq[MilestoneDefinition]] =
    Future {
      getRecords(SQL_GET_ALL_MILESTONES_BY_CATEGORY)(milestonesRowParser).map(_.toDomain)
    }

  override def getMilestoneTypesByCategory(category: MetricCategory): Future[Seq[MilestoneDefinition]] =
    Future {
      getRecords(SQL_GET_ALL_MILESTONES_BY_CATEGORY, "milestoneCategory" -> category.stringValue)(milestonesRowParser)
        .map(_.toDomain)
    }

  override def deleteAllUserMilestones(userId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(SQL_DELETE_ALL_USER_MILESTONES, Seq("userId" -> userId))
    }
}

object AnormUserMilestonesRepository {

  private val SQL_DELETE_ALL_USER_MILESTONES =
    s"""
       |delete from user_milestones
       |where user_id = {userId}::uuid ;
       |""".stripMargin

  private val SQL_GET_ALL_MILESTONES =
    s"""
       |select *
       |from milestones ;
       |""".stripMargin

  private val SQL_GET_ALL_MILESTONES_BY_CATEGORY =
    s"""
       |select *
       |from milestones
       |where milestone_category = {milestoneCategory} ;
       |""".stripMargin

  private val SQL_GET_ALL_USER_MILESTONES =
    s"""
       |select *
       |from user_milestones
       |where user_id = {userId}::uuid 
       |order by created_at asc ;
       |""".stripMargin

  private val SQL_GET_ALL_USER_MILESTONES_BY_CATEGORY =
    s"""
       |select *
       |from user_milestones
       |where user_id = {userId}::uuid 
       |and milestone_category = {milestoneCategory} 
       |order by created_at asc ;
       |""".stripMargin

  private val SQL_CREATE_AND_RETURN_NEW_USER_MILESTONE =
    s"""
       |insert into user_milestones (user_id, milestone_name, milestone_category, created_at, updated_at)
       |values ({userId}::uuid, {milestoneName}, {milestoneCategory}, {now}, {now})
       |returning * ;
       |""".stripMargin

  private case class UserMilestonesRow(
    user_id: UUID,
    milestone_name: String,
    milestone_category: String,
    created_at: Instant,
    updated_at: Instant
  ) {
    def toDomain: UserMilestone =
      UserMilestone(
        userId = user_id,
        Milestone.apply(milestone_name),
        MetricCategory.apply(milestone_category),
        createdAt = created_at,
        updatedAt = updated_at
      )
  }

  private case class MilestoneRow(name: String, milestone_category: String, description: String) {
    def toDomain: MilestoneDefinition =
      MilestoneDefinition(
        name = Milestone.apply(name),
        category = MetricCategory.apply(milestone_category),
        description = description
      )
  }

  private val userMilestonesRowParser: RowParser[UserMilestonesRow] = Macro.namedParser[UserMilestonesRow]
  private val milestonesRowParser: RowParser[MilestoneRow] = Macro.namedParser[MilestoneRow]

}
