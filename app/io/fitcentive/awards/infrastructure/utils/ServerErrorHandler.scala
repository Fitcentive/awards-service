package io.fitcentive.awards.infrastructure.utils

import io.fitcentive.awards.domain.errors.{ChatServiceError, DiaryServiceError, FourSquareServiceError}
import io.fitcentive.sdk.error.{DomainError, EntityConflictError, EntityNotAccessible, EntityNotFoundError}
import io.fitcentive.sdk.logging.AppLogger
import io.fitcentive.sdk.utils.DomainErrorHandler
import play.api.mvc.Result
import play.api.mvc.Results.{Conflict, Forbidden, InternalServerError, NotFound}

trait ServerErrorHandler extends DomainErrorHandler with AppLogger {

  override def resultErrorAsyncHandler: PartialFunction[Throwable, Result] = {
    case e: Exception =>
      logError(s"${e.getMessage}", e)
      InternalServerError(e.getMessage)
  }

  override def domainErrorHandler: PartialFunction[DomainError, Result] = {
    case EntityNotFoundError(reason)    => NotFound(reason)
    case EntityConflictError(reason)    => Conflict(reason)
    case EntityNotAccessible(reason)    => Forbidden(reason)
    case FourSquareServiceError(reason) => InternalServerError(reason)
    case DiaryServiceError(reason)      => InternalServerError(reason)
    case ChatServiceError(reason)       => InternalServerError(reason)
    case _                              => InternalServerError("Unexpected error occurred ")
  }

}
