package io.fitcentive.awards.services

import com.google.inject.ImplementedBy
import com.typesafe.config.Config
import io.fitcentive.awards.domain.config.{AppPubSubConfig, EnvironmentConfig, FourSquareConfig}
import io.fitcentive.awards.infrastructure.settings.AppConfigService
import io.fitcentive.sdk.config.{GcpConfig, JwtConfig, SecretConfig, ServerConfig}

@ImplementedBy(classOf[AppConfigService])
trait SettingsService {
  def pubSubServiceAccountStringCredentials: String
  def keycloakConfigRaw: Config
  def jwtConfig: JwtConfig
  def keycloakServerUrl: String
  def serverConfig: ServerConfig
  def userServiceConfig: ServerConfig
  def diaryServiceConfig: ServerConfig
  def secretConfig: SecretConfig
  def fourSquareConfig: FourSquareConfig
  def chatServiceConfig: ServerConfig
  def gcpConfig: GcpConfig
  def pubSubConfig: AppPubSubConfig
  def serviceAccountStringCredentials: String
  def staticDeletedUserId: String
  def staticDeletedUserEmail: String
  def envConfig: EnvironmentConfig
}
