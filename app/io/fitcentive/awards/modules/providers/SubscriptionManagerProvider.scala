package io.fitcentive.awards.modules.providers

import com.google.auth.oauth2.ServiceAccountCredentials
import io.fitcentive.awards.api.MetricsApi
import io.fitcentive.awards.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.awards.infrastructure.handlers.MessageEventHandlers
import io.fitcentive.awards.infrastructure.pubsub.SubscriptionManager
import io.fitcentive.awards.services.SettingsService
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}

import java.io.ByteArrayInputStream
import javax.inject.{Inject, Provider}
import scala.concurrent.ExecutionContext

class SubscriptionManagerProvider @Inject() (
  publisher: PubSubPublisher,
  settingsService: SettingsService,
  _metricsApi: MetricsApi,
)(implicit ec: PubSubExecutionContext)
  extends Provider[SubscriptionManager] {

  trait SubscriptionEventHandlers extends MessageEventHandlers {
    override def metricsApi: MetricsApi = _metricsApi
    override implicit def executionContext: ExecutionContext = ec
  }

  override def get(): SubscriptionManager = {
    val credentials =
      ServiceAccountCredentials
        .fromStream(new ByteArrayInputStream(settingsService.serviceAccountStringCredentials.getBytes()))
        .createScoped()
    new SubscriptionManager(
      publisher = publisher,
      subscriber = new PubSubSubscriber(credentials, settingsService.gcpConfig.project),
      config = settingsService.pubSubConfig,
      environment = settingsService.envConfig.environment
    ) with SubscriptionEventHandlers
  }
}
