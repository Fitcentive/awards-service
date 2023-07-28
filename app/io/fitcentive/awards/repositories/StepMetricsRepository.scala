package io.fitcentive.awards.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.meetup.MeetupParticipant
import io.fitcentive.awards.infrastructure.database.sql.AnormParticipantsRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormParticipantsRepository])
trait ParticipantsRepository {
  def replaceParticipantWithDeletedUserId(participantId: UUID, deletedUserId: UUID): Future[Unit]
  def getParticipantsForMeetup(meetupId: UUID): Future[Seq[MeetupParticipant]]
  def getParticipantForMeetup(meetupId: UUID, participantId: UUID): Future[Option[MeetupParticipant]]
  def insertParticipantIntoMeetup(meetupId: UUID, participantId: UUID): Future[MeetupParticipant]
  def deleteParticipantFromMeetup(meetupId: UUID, participantId: UUID): Future[Unit]
}
