package io.fitcentive.awards.infrastructure.database.sql

import anorm.{Macro, RowParser}
import io.fitcentive.awards.domain.meetup.MeetupParticipant
import io.fitcentive.awards.repositories.ParticipantsRepository
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import io.fitcentive.sdk.infrastructure.database.DatabaseClient
import play.api.db.Database

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class AnormParticipantsRepository @Inject() (val db: Database)(implicit val dbec: DatabaseExecutionContext)
  extends ParticipantsRepository
  with DatabaseClient {

  import AnormParticipantsRepository._

  override def replaceParticipantWithDeletedUserId(participantId: UUID, deletedUserId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(
        SQL_REPLACE_PARTICIPANT_WITH_DELETED_USER_ID,
        Seq("participantId" -> participantId, "deletedUserId" -> deletedUserId)
      )
    }

  override def getParticipantsForMeetup(meetupId: UUID): Future[Seq[MeetupParticipant]] =
    Future {
      getRecords(SQL_GET_MEETUP_PARTICIPANTS, "meetupId" -> meetupId)(participantRowParser).map(_.toDomain)
    }

  override def getParticipantForMeetup(meetupId: UUID, participantId: UUID): Future[Option[MeetupParticipant]] =
    Future {
      getRecordOpt(SQL_GET_MEETUP_PARTICIPANT, "meetupId" -> meetupId, "participantId" -> participantId)(
        participantRowParser
      ).map(_.toDomain)
    }

  override def deleteParticipantFromMeetup(meetupId: UUID, participantId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(
        SQL_DELETE_PARTICIPANT_FROM_MEETUP,
        Seq("userId" -> participantId, "meetupId" -> meetupId)
      )
    }

  override def insertParticipantIntoMeetup(meetupId: UUID, participantId: UUID): Future[MeetupParticipant] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[ParticipantsRow](
          SQL_INSERT_AND_RETURN_MEETUP_PARTICIPANT,
          Seq("meetupId" -> meetupId, "userId" -> participantId, "now" -> now)
        )(participantRowParser).toDomain
      }
    }
}

object AnormParticipantsRepository {

  private val SQL_REPLACE_PARTICIPANT_WITH_DELETED_USER_ID: String =
    s"""
       |update meetup_participants 
       |set user_id = {deletedUserId}::uuid 
       |where user_id = {participantId}::uuid ;
       |""".stripMargin

  private val SQL_GET_MEETUP_PARTICIPANT: String =
    s"""
       |select *
       |from meetup_participants m
       |where m.meetup_id = {meetupId}::uuid
       |and m.user_id = {participantId}::uuid
       |""".stripMargin

  private val SQL_GET_MEETUP_PARTICIPANTS: String =
    s"""
       |select *
       |from meetup_participants m
       |where m.meetup_id = {meetupId}::uuid
       |""".stripMargin

  private val SQL_DELETE_PARTICIPANT_FROM_MEETUP: String =
    s"""
       |delete from meetup_participants m
       |where m.user_id = {userId}::uuid and m.meetup_id = {meetupId}::uuid
       |""".stripMargin

  private val SQL_INSERT_AND_RETURN_MEETUP_PARTICIPANT: String =
    s"""
       |insert into meetup_participants(meetup_id, user_id, created_at, updated_at)
       |values ({meetupId}::uuid, {userId}::uuid, {now}, {now})
       |returning *;
       |""".stripMargin

  private case class ParticipantsRow(meetup_id: UUID, user_id: UUID, created_at: Instant, updated_at: Instant) {
    def toDomain: MeetupParticipant =
      MeetupParticipant(meetupId = meetup_id, userId = user_id, createdAt = created_at, updatedAt = updated_at)
  }

  private val participantRowParser: RowParser[ParticipantsRow] = Macro.namedParser[ParticipantsRow]

}
