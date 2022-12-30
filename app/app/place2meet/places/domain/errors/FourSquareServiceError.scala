package app.place2meet.places.domain.errors

import app.place2meet.sdk.error.DomainError

import java.util.UUID

case class FourSquareServiceError(reason: String) extends DomainError {
  override def code: UUID = UUID.fromString("eee55ea8-6431-47e0-a1de-f35f82f310e4")
}
