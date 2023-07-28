package io.fitcentive.awards.infrastructure.pubsub

import io.fitcentive.awards.domain.config.TopicsConfig
import io.fitcentive.awards.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.awards.services.{MessageBusService, SettingsService}
import io.fitcentive.registry.events.meetup._
import io.fitcentive.registry.events.scheduled.reminder.{
  CancelPreviouslyScheduledMeetupReminderForLater,
  ScheduleMeetupReminderForLater
}
import io.fitcentive.registry.events.scheduled.transition.{
  CancelPreviouslyScheduledMeetupStateTransitionForLater,
  ScheduleMeetupStateTransitionTimeForLater
}
import io.fitcentive.sdk.gcp.pubsub.PubSubPublisher

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class EventPublisherService @Inject() (publisher: PubSubPublisher, settingsService: SettingsService)(implicit
  ec: PubSubExecutionContext
) extends MessageBusService {

  private val publisherConfig: TopicsConfig = settingsService.pubSubConfig.topicsConfig

  override def publishCancelPreviouslyScheduledMeetupStateTransitionForLater(meetupId: UUID): Future[Unit] =
    CancelPreviouslyScheduledMeetupStateTransitionForLater(meetupId)
      .pipe(publisher.publish(publisherConfig.cancelScheduledMeetupStateTransitionForLaterTopic, _))

  override def publishScheduleMeetupStateTransitionForLater(meetupId: UUID, later: Long): Future[Unit] =
    ScheduleMeetupStateTransitionTimeForLater(meetupId, later)
      .pipe(publisher.publish(publisherConfig.scheduleMeetupStateTransitionForLaterTopic, _))

  override def publishCancelPreviouslyScheduledMeetupReminderForLater(meetupId: UUID): Future[Unit] =
    CancelPreviouslyScheduledMeetupReminderForLater(meetupId)
      .pipe(publisher.publish(publisherConfig.cancelScheduledMeetupReminderForLaterTopic, _))

  override def publishScheduleMeetupReminderForLater(meetupId: UUID, later: Long): Future[Unit] =
    ScheduleMeetupReminderForLater(meetupId, later)
      .pipe(publisher.publish(publisherConfig.scheduleMeetupReminderForLaterTopic, _))

  override def publishMeetupReminder(meetupId: UUID, meetupName: String, targetUser: UUID): Future[Unit] =
    MeetupReminder(meetupId, meetupName, targetUser)
      .pipe(publisher.publish(publisherConfig.meetupReminderTopic, _))

  override def publishMeetupLocationHasChanged(
    meetupId: UUID,
    meetupName: String,
    meetupOwnerId: UUID,
    targetUser: UUID
  ): Future[Unit] =
    MeetupLocationChanged(meetupId, meetupOwnerId, meetupName, targetUser)
      .pipe(publisher.publish(publisherConfig.meetupLocationChangedTopic, _))

  override def publishMeetupDecisionMade(
    meetupName: String,
    meetupId: UUID,
    meetupOwnerId: UUID,
    meetupParticipantId: UUID,
    hasAccepted: Boolean
  ): Future[Unit] =
    MeetupDecision(meetupName, meetupId, meetupOwnerId, meetupParticipantId, hasAccepted)
      .pipe(publisher.publish(publisherConfig.meetupDecisionTopic, _))

  override def publishParticipantAddedToMeetup(
    meetupName: String,
    meetupId: UUID,
    meetupOwnerId: UUID,
    meetupParticipantId: UUID
  ): Future[Unit] =
    ParticipantAddedToMeetup(meetupName, meetupId, meetupOwnerId, meetupParticipantId)
      .pipe(publisher.publish(publisherConfig.participantAddedToMeetupTopic, _))

  override def publishParticipantAddedAvailabilityToMeetup(
    meetupName: String,
    meetupId: UUID,
    meetupOwnerId: UUID,
    meetupParticipantId: UUID,
    targetUserId: UUID
  ): Future[Unit] =
    ParticipantAddedAvailabilityToMeetup(meetupName, meetupId, meetupOwnerId, meetupParticipantId, targetUserId)
      .pipe(publisher.publish(publisherConfig.participantAddedAvailabilityToMeetupTopic, _))
}
