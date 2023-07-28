package io.fitcentive.awards.controllers

import io.fitcentive.awards.api.{AchievementsApi, MetricsApi}
import io.fitcentive.awards.infrastructure.utils.ServerErrorHandler
import io.fitcentive.sdk.play.{InternalAuthAction, UserAuthAction}
import io.fitcentive.sdk.utils.PlayControllerOps
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AchievementsController @Inject() (
  achievementsApi: AchievementsApi,
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
  def getUserAchievementMilestonesForAllCategories(milestoneCategory: Option[String] = None): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      achievementsApi
        .getUserAllTimeAchievementMilestones(userRequest.authorizedUser.userId, milestoneCategory)
        .map(userMilestones => Ok(Json.toJson(userMilestones)))
        .recover(resultErrorAsyncHandler)
    }

  def getAllAchievementMilestoneTypes(milestoneCategory: Option[String] = None): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      achievementsApi
        .getAllDifferentMilestoneTypes(milestoneCategory)
        .map(milestoneTypes => Ok(Json.toJson(milestoneTypes)))
        .recover(resultErrorAsyncHandler)
    }

  //---------------------
  // Internal routes
  //---------------------

}
