package io.fitcentive.awards.services

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.config.{AppPubSubConfig, EnvironmentConfig}
import io.fitcentive.awards.infrastructure.settings.AppConfigService
import io.fitcentive.sdk.config.{GcpConfig, JwtConfig, SecretConfig, ServerConfig}

@ImplementedBy(classOf[AppConfigService])
trait SettingsService {
  def pubSubServiceAccountStringCredentials: String
  def jwtConfig: JwtConfig
  def keycloakServerUrl: String
  def serverConfig: ServerConfig
  def secretConfig: SecretConfig
  def gcpConfig: GcpConfig
  def pubSubConfig: AppPubSubConfig
  def serviceAccountStringCredentials: String
  def envConfig: EnvironmentConfig
}
