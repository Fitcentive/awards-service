package io.fitcentive.awards.infrastructure.settings

import com.typesafe.config.Config
import io.fitcentive.awards.domain.config._
import io.fitcentive.awards.services.SettingsService
import io.fitcentive.sdk.config.{GcpConfig, JwtConfig, SecretConfig, ServerConfig}
import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfigService @Inject() (config: Configuration) extends SettingsService {

  override def staticDeletedUserId: String = config.get[String]("meetup.deleted-user-static-id")

  override def staticDeletedUserEmail: String = config.get[String]("meetup.deleted-user-static-email")

  override def serviceAccountStringCredentials: String =
    config.get[String]("gcp.pubsub.service-account-string-credentials")

  override def envConfig: EnvironmentConfig = EnvironmentConfig.fromConfig(config.get[Config]("environment"))

  override def chatServiceConfig: ServerConfig =
    ServerConfig.fromConfig(config.get[Config]("services.chat-service"))

  override def pubSubServiceAccountStringCredentials: String =
    config.get[String]("gcp.pubsub.service-account-string-credentials")

  override def gcpConfig: GcpConfig = GcpConfig(project = config.get[String]("gcp.project"))

  override def keycloakServerUrl: String = config.get[String]("keycloak.server-url")

  override def secretConfig: SecretConfig = SecretConfig.fromConfig(config.get[Config]("services"))

  override def fourSquareConfig: FourSquareConfig = FourSquareConfig.fromConfig(config.get[Config]("foursquare"))

  override def userServiceConfig: ServerConfig = ServerConfig.fromConfig(config.get[Config]("services.user-service"))

  override def diaryServiceConfig: ServerConfig = ServerConfig.fromConfig(config.get[Config]("services.diary-service"))

  override def serverConfig: ServerConfig = ServerConfig.fromConfig(config.get[Config]("http-server"))

  override def keycloakConfigRaw: Config = config.get[Config]("keycloak")

  override def jwtConfig: JwtConfig = JwtConfig.apply(config.get[Config]("jwt"))

  override def pubSubConfig: AppPubSubConfig =
    AppPubSubConfig(
      topicsConfig = TopicsConfig.fromConfig(config.get[Config]("gcp.pubsub.topics")),
      subscriptionsConfig = SubscriptionsConfig.fromConfig(config.get[Config]("gcp.pubsub.subscriptions"))
    )
}
