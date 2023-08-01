package io.fitcentive.awards.controllers

import io.fitcentive.awards.api.{AchievementsApi, MetricsApi, ProgressApi}
import io.fitcentive.awards.infrastructure.utils.ServerErrorHandler
import io.fitcentive.sdk.play.{InternalAuthAction, UserAuthAction}
import io.fitcentive.sdk.utils.PlayControllerOps
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ProgressController @Inject() (
  progressApi: ProgressApi,
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
  def getUserStepProgressMetrics(from: String, to: String): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      progressApi
        .getUserStepProgressMetrics(userRequest.authorizedUser.userId, from, to)
        .map(metrics => Ok(Json.toJson(metrics)))
        .recover(resultErrorAsyncHandler)
    }

  def getUserDiaryProgressMetrics(from: String, to: String): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      progressApi
        .getUserDiaryEntryProgressMetrics(userRequest.authorizedUser.userId, from, to)
        .map(metrics => Ok(Json.toJson(metrics)))
        .recover(resultErrorAsyncHandler)
    }

  def getUserActivityProgressMetrics(from: String, to: String): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      progressApi
        .getUserActivityProgressMetrics(userRequest.authorizedUser.userId, from, to)
        .map(metrics => Ok(Json.toJson(metrics)))
        .recover(resultErrorAsyncHandler)
    }

  def getUserProgressInsights(offsetInMinutes: Int): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      progressApi
        .getUserProgressInsights(userRequest.authorizedUser.userId, offsetInMinutes)
        .map(insights => Ok(Json.toJson(insights)))
        .recover(resultErrorAsyncHandler)
    }

  //---------------------
  // Internal routes
  //---------------------

}
