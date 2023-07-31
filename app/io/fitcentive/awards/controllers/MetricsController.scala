package io.fitcentive.awards.controllers

import io.fitcentive.awards.api.MetricsApi
import io.fitcentive.awards.infrastructure.utils.ServerErrorHandler
import io.fitcentive.sdk.play.{InternalAuthAction, UserAuthAction}
import io.fitcentive.sdk.utils.PlayControllerOps
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class MetricsController @Inject() (
  metricsApi: MetricsApi,
  userAuthAction: UserAuthAction,
  internalAuthAction: InternalAuthAction,
  cc: ControllerComponents
)(implicit exec: ExecutionContext)
  extends AbstractController(cc)
  with PlayControllerOps
  with ServerErrorHandler {

  //---------------------
  // User auth routes
  //---------------------

  //---------------------
  // Internal routes
  //---------------------
  def deleteUserData(userId: UUID): Action[AnyContent] =
    internalAuthAction.async { implicit request =>
      metricsApi
        .deleteAllUserData(userId)
        .map(_ => NoContent)
        .recover(resultErrorAsyncHandler)
    }

}
