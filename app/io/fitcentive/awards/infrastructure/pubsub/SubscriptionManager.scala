package io.fitcentive.awards.infrastructure.pubsub

import io.fitcentive.awards.domain.config.AppPubSubConfig
import io.fitcentive.awards.domain.events.{
  EventHandlers,
  ScheduledMeetupReminderEvent,
  ScheduledMeetupStateTransitionEvent
}
import io.fitcentive.awards.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

class SubscriptionManager(
  publisher: PubSubPublisher,
  subscriber: PubSubSubscriber,
  config: AppPubSubConfig,
  environment: String
)(implicit ec: PubSubExecutionContext)
  extends AppLogger {

  self: EventHandlers =>

  initializeSubscriptions

  final def initializeSubscriptions: Future[Unit] = {
    for {
      _ <- Future.sequence(config.topicsConfig.topics.map(publisher.createTopic))
      _ <-
        subscriber
          .subscribe[ScheduledMeetupReminderEvent](
            environment,
            config.subscriptionsConfig.scheduledMeetupReminderSubscription,
            config.topicsConfig.scheduledMeetupReminderTopic
          )(_.payload.pipe(handleEvent))
      _ <-
        subscriber
          .subscribe[ScheduledMeetupStateTransitionEvent](
            environment,
            config.subscriptionsConfig.scheduledMeetupStateTransitionSubscription,
            config.topicsConfig.scheduledMeetupStateTransitionTopic
          )(_.payload.pipe(handleEvent))
      _ = logInfo("Subscriptions set up successfully!")
    } yield ()
  }
}
