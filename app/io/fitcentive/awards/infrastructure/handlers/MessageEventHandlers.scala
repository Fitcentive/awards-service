package io.fitcentive.awards.infrastructure.handlers

import io.fitcentive.awards.api.MeetupApi
import io.fitcentive.awards.domain.events.{
  EventHandlers,
  EventMessage,
  ScheduledMeetupReminderEvent,
  ScheduledMeetupStateTransitionEvent
}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait MessageEventHandlers extends EventHandlers {

  def meetupApi: MeetupApi
  implicit def executionContext: ExecutionContext

  override def handleEvent(event: EventMessage): Future[Unit] =
    event match {
      case event: ScheduledMeetupStateTransitionEvent =>
        meetupApi.transitionMeetupToFinalState(UUID.fromString(event.meetupId)).map(_ => ())

      case event: ScheduledMeetupReminderEvent =>
        meetupApi.sendMeetupReminderToAllParticipants(UUID.fromString(event.meetupId)).map(_ => ())

      case _ => Future.failed(new Exception("Unrecognized event"))
    }
}
