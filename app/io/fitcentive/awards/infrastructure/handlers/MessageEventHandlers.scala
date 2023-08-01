package io.fitcentive.awards.infrastructure.handlers

import io.fitcentive.awards.api.MetricsApi
import io.fitcentive.awards.domain.events.{
  EventHandlers,
  EventMessage,
  UserDiaryEntryCreatedEvent,
  UserStepDataUpdatedEvent
}

import scala.concurrent.{ExecutionContext, Future}

trait MessageEventHandlers extends EventHandlers {

  def metricsApi: MetricsApi
  implicit def executionContext: ExecutionContext

  override def handleEvent(event: EventMessage): Future[Unit] =
    event match {

      case event: UserDiaryEntryCreatedEvent =>
        metricsApi.handleUserDiaryEntryCreatedEvent(event.userId, event.date, event.activityMinutes).map(_ => ())

      case event: UserStepDataUpdatedEvent =>
        metricsApi.handleUserStepDataUpdatedEvent(event.userId, event.date, event.stepsTaken).map(_ => ())

      case _ => Future.failed(new Exception("Unrecognized event"))
    }
}
