package io.fitcentive.awards.controllers

import io.fitcentive.awards.api.DiaryApi
import io.fitcentive.awards.infrastructure.utils.ServerErrorHandler
import io.fitcentive.sdk.play.{InternalAuthAction, UserAuthAction}
import io.fitcentive.sdk.utils.PlayControllerOps
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DiaryController @Inject() (
  diaryApi: DiaryApi,
  userAuthAction: UserAuthAction,
  internalAuthAction: InternalAuthAction,
  cc: ControllerComponents
)(implicit exec: ExecutionContext)
  extends AbstractController(cc)
  with PlayControllerOps
  with ServerErrorHandler {

  def getDiaryEntriesForMeetup(meetupId: UUID, userId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .getDiaryEntriesForMeetup(userRequest.authorizedUser.userId, userId, meetupId)
        .map(handleEitherResult(_)(diaryEntries => Ok(Json.toJson(diaryEntries))))
        .recover(resultErrorAsyncHandler)
    }

  def upsertCardioDiaryEntryForMeetup(meetupId: UUID, diaryEntryId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .upsertCardioDiaryEntryForMeetup(userRequest.authorizedUser.userId, meetupId, diaryEntryId)
        .map(handleEitherResult(_)(entry => Ok(Json.toJson(entry))))
        .recover(resultErrorAsyncHandler)
    }

  def deleteCardioDiaryEntryForMeetup(meetupId: UUID, diaryEntryId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .deleteCardioDiaryEntryForMeetup(userRequest.authorizedUser.userId, meetupId, diaryEntryId)
        .map(handleEitherResult(_)(_ => Accepted))
        .recover(resultErrorAsyncHandler)
    }

  def upsertStrengthDiaryEntryForMeetup(meetupId: UUID, diaryEntryId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .upsertStrengthDiaryEntryForMeetup(userRequest.authorizedUser.userId, meetupId, diaryEntryId)
        .map(handleEitherResult(_)(entry => Ok(Json.toJson(entry))))
        .recover(resultErrorAsyncHandler)
    }

  def deleteStrengthDiaryEntryForMeetup(meetupId: UUID, diaryEntryId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .deleteStrengthDiaryEntryForMeetup(userRequest.authorizedUser.userId, meetupId, diaryEntryId)
        .map(handleEitherResult(_)(_ => Accepted))
        .recover(resultErrorAsyncHandler)
    }

  def upsertFoodDiaryEntryForMeetup(meetupId: UUID, diaryEntryId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .upsertFoodDiaryEntryForMeetup(userRequest.authorizedUser.userId, meetupId, diaryEntryId)
        .map(handleEitherResult(_)(entry => Ok(Json.toJson(entry))))
        .recover(resultErrorAsyncHandler)
    }

  def deleteFoodDiaryEntryForMeetup(meetupId: UUID, diaryEntryId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .deleteFoodDiaryEntryForMeetup(userRequest.authorizedUser.userId, meetupId, diaryEntryId)
        .map(handleEitherResult(_)(_ => Accepted))
        .recover(resultErrorAsyncHandler)
    }

  def removeAssociatedDiaryEntriesForMeetupByUser(meetupId: java.util.UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      diaryApi
        .removeAssociatedDiaryEntriesForMeetupByUser(userRequest.authorizedUser.userId, meetupId)
        .map(_ => NoContent)
        .recover(resultErrorAsyncHandler)
    }

  //---------------------
  // Internal routes
  //------------------------
  def deleteCardioDiaryEntryById(diaryEntryId: UUID): Action[AnyContent] =
    internalAuthAction.async { implicit userRequest =>
      diaryApi
        .deleteCardioDiaryEntryById(diaryEntryId)
        .map(_ => NoContent)
        .recover(resultErrorAsyncHandler)
    }

  def deleteStrengthDiaryEntryById(diaryEntryId: UUID): Action[AnyContent] =
    internalAuthAction.async { implicit userRequest =>
      diaryApi
        .deleteStrengthDiaryEntryById(diaryEntryId)
        .map(_ => NoContent)
        .recover(resultErrorAsyncHandler)
    }

  def deleteFoodDiaryEntryById(diaryEntryId: UUID): Action[AnyContent] =
    internalAuthAction.async { implicit userRequest =>
      diaryApi
        .deleteFoodDiaryEntryById(diaryEntryId)
        .map(_ => NoContent)
        .recover(resultErrorAsyncHandler)
    }

  def deleteAllMeetupDataForUser(userId: UUID): Action[AnyContent] =
    internalAuthAction.async { implicit userRequest =>
      diaryApi
        .deleteAllMeetupDataForUser(userId)
        .map(_ => NoContent)
        .recover(resultErrorAsyncHandler)
    }

}
