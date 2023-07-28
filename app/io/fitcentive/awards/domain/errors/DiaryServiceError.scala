package io.fitcentive.awards.domain.errors

import io.fitcentive.sdk.error.DomainError

import java.util.UUID

case class DiaryServiceError(reason: String) extends DomainError {
  override def code: UUID = UUID.fromString("8cfd9fb1-1caf-4267-81cf-8668bdcc8e6a")
}
