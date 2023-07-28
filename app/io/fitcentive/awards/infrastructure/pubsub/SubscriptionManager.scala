package io.fitcentive.awards.infrastructure.pubsub

import io.fitcentive.awards.domain.config.AppPubSubConfig
import io.fitcentive.awards.domain.events.{EventHandlers, UserStepDataUpdatedEvent}
import io.fitcentive.awards.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.registry.events.steps.UserStepDataUpdated
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger
import io.fitcentive.awards.infrastructure.AntiCorruptionDomain

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

class SubscriptionManager(
  publisher: PubSubPublisher,
  subscriber: PubSubSubscriber,
  config: AppPubSubConfig,
  environment: String
)(implicit ec: PubSubExecutionContext)
  extends AppLogger
  with AntiCorruptionDomain {

  self: EventHandlers =>

  initializeSubscriptions

  final def initializeSubscriptions: Future[Unit] = {
    for {
      _ <- Future.sequence(config.topicsConfig.topics.map(publisher.createTopic))
      _ <- subscribeToUserStepData
      _ = logInfo("Subscriptions set up successfully!")
    } yield ()
  }

  private def subscribeToUserStepData: Future[Unit] =
    subscriber
      .subscribe[UserStepDataUpdated](
        environment,
        config.subscriptionsConfig.userStepDataUpdatedSubscription,
        config.topicsConfig.userStepDataUpdatedTopic
      )(_.payload.toDomain.pipe(handleEvent))
}
