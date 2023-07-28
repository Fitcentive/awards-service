package io.fitcentive.awards.services

import com.google.inject.ImplementedBy
import io.fitcentive.awards.infrastructure.pubsub.EventPublisherService

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[EventPublisherService])
trait MessageBusService {
  def publishMeetupDecisionMade(
    meetupName: String,
    meetupId: UUID,
    meetupOwnerId: UUID,
    meetupParticipantId: UUID,
    hasAccepted: Boolean
  ): Future[Unit]
  def publishParticipantAddedToMeetup(
    meetupName: String,
    meetupId: UUID,
    meetupOwnerId: UUID,
    meetupParticipantId: UUID
  ): Future[Unit]
  def publishParticipantAddedAvailabilityToMeetup(
    meetupName: String,
    meetupId: UUID,
    meetupOwnerId: UUID,
    meetupParticipantId: UUID,
    targetUserId: UUID,
  ): Future[Unit]
  def publishMeetupReminder(meetupId: UUID, meetupName: String, targetUser: UUID): Future[Unit]
  def publishMeetupLocationHasChanged(
    meetupId: UUID,
    meetupName: String,
    meetupOwnerId: UUID,
    targetUser: UUID
  ): Future[Unit]
  def publishCancelPreviouslyScheduledMeetupReminderForLater(meetupId: UUID): Future[Unit]
  def publishScheduleMeetupReminderForLater(meetupId: UUID, later: Long): Future[Unit]
  def publishCancelPreviouslyScheduledMeetupStateTransitionForLater(meetupId: UUID): Future[Unit]
  def publishScheduleMeetupStateTransitionForLater(meetupId: UUID, later: Long): Future[Unit]
}
