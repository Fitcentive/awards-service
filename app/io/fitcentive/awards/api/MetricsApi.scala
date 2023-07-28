package io.fitcentive.awards.api

import cats.data.EitherT
import io.fitcentive.awards.domain.diary.AllDiaryEntriesForMeetup
import io.fitcentive.awards.domain.meetup._
import io.fitcentive.awards.repositories._
import io.fitcentive.awards.services.{DiaryService, SettingsService}
import io.fitcentive.sdk.error.{DomainError, EntityNotAccessible, EntityNotFoundError}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class DiaryApi @Inject() (
  meetupCardioDiaryEntryRepository: MeetupCardioDiaryEntryRepository,
  meetupStrengthDiaryEntryRepository: MeetupStrengthDiaryEntryRepository,
  meetupFoodDiaryEntryRepository: MeetupFoodDiaryEntryRepository,
  meetupRepository: MeetupRepository,
  participantsRepository: ParticipantsRepository,
  availabilityRepository: AvailabilityRepository,
  commentsRepository: MeetupCommentsRepository,
  decisionsRepository: MeetupDecisionsRepository,
  diaryService: DiaryService,
  settingsService: SettingsService,
)(implicit ec: ExecutionContext) {

  // Gets cardio, strength and food diary entries for the meetup
  def getDiaryEntriesForMeetup(
    currentUserId: UUID,
    userId: UUID,
    meetupId: UUID
  ): Future[Either[DomainError, AllDiaryEntriesForMeetup]] =
    (for {
      _ <- EitherT[Future, DomainError, Meetup](
        meetupRepository
          .getMeetup(meetupId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Meetup not found!"))))
      )
      meetupParticipants <- EitherT.right[DomainError](participantsRepository.getParticipantsForMeetup(meetupId))
      _ <- EitherT[Future, DomainError, Boolean](
        // Ensure currentUser == userId or currentUser is a meetup participant
        (currentUserId == userId || meetupParticipants.exists(_.userId == currentUserId))
          .pipe {
            case true  => Right.apply(true)
            case false => Left(EntityNotAccessible("Cannot access diary entries for meetup not a part of"))
          }
          .pipe(Future.successful)
      )
      cardioEntries <-
        EitherT.right[DomainError](meetupCardioDiaryEntryRepository.getUserMeetupCardioDiaryEntries(userId, meetupId))
      strengthEntries <- EitherT.right[DomainError](
        meetupStrengthDiaryEntryRepository.getUserMeetupStrengthDiaryEntries(userId, meetupId)
      )
      foodEntries <-
        EitherT.right[DomainError](meetupFoodDiaryEntryRepository.getUserMeetupFoodDiaryEntries(userId, meetupId))

      allDiaryEntries <- EitherT[Future, DomainError, AllDiaryEntriesForMeetup](
        diaryService.getDiaryEntriesByIds(
          foodDiaryEntries = foodEntries.map(_.foodEntryId),
          strengthDiaryEntries = strengthEntries.map(_.strengthEntryId),
          cardioDiaryEntries = cardioEntries.map(_.cardioEntryId)
        )
      )

    } yield allDiaryEntries).value

  def deleteCardioDiaryEntryForMeetup(
    currentUserId: UUID,
    meetupId: UUID,
    cardioDiaryEntryId: UUID
  ): Future[Either[DomainError, Unit]] =
    (for {
      _ <- EitherT[Future, DomainError, Meetup](
        meetupRepository
          .getMeetup(meetupId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Meetup not found!"))))
      )
      _ <- EitherT[Future, DomainError, Seq[MeetupParticipant]](
        participantsRepository
          .getParticipantsForMeetup(meetupId)
          .map { participants =>
            if (participants.exists(_.userId == currentUserId)) Right(participants)
            else Left(EntityNotFoundError("Participant for Meetup not found!"))
          }
      )
      _ <- EitherT.right[DomainError](
        meetupCardioDiaryEntryRepository.deleteUserMeetupCardioDiaryEntry(currentUserId, meetupId, cardioDiaryEntryId)
      )
      _ <- EitherT.right[DomainError](diaryService.dissociateCardioDiaryEntryFromMeetup(cardioDiaryEntryId))
    } yield ()).value

  def deleteStrengthDiaryEntryForMeetup(
    currentUserId: UUID,
    meetupId: UUID,
    strengthDiaryEntryId: UUID
  ): Future[Either[DomainError, Unit]] =
    (for {
      _ <- EitherT[Future, DomainError, Meetup](
        meetupRepository
          .getMeetup(meetupId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Meetup not found!"))))
      )
      _ <- EitherT[Future, DomainError, Seq[MeetupParticipant]](
        participantsRepository
          .getParticipantsForMeetup(meetupId)
          .map { participants =>
            if (participants.exists(_.userId == currentUserId)) Right(participants)
            else Left(EntityNotFoundError("Participant for Meetup not found!"))
          }
      )
      _ <- EitherT.right[DomainError](
        meetupStrengthDiaryEntryRepository
          .deleteUserMeetupStrengthDiaryEntry(currentUserId, meetupId, strengthDiaryEntryId)
      )
      _ <- EitherT.right[DomainError](diaryService.dissociateStrengthDiaryEntryFromMeetup(strengthDiaryEntryId))
    } yield ()).value

  def deleteFoodDiaryEntryForMeetup(
    currentUserId: UUID,
    meetupId: UUID,
    foodDiaryEntryId: UUID
  ): Future[Either[DomainError, Unit]] =
    (for {
      _ <- EitherT[Future, DomainError, Meetup](
        meetupRepository
          .getMeetup(meetupId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Meetup not found!"))))
      )
      _ <- EitherT[Future, DomainError, Seq[MeetupParticipant]](
        participantsRepository
          .getParticipantsForMeetup(meetupId)
          .map { participants =>
            if (participants.exists(_.userId == currentUserId)) Right(participants)
            else Left(EntityNotFoundError("Participant for Meetup not found!"))
          }
      )
      _ <- EitherT.right[DomainError](
        meetupFoodDiaryEntryRepository
          .deleteUserMeetupFoodDiaryEntry(currentUserId, meetupId, foodDiaryEntryId)
      )
      _ <- EitherT.right[DomainError](diaryService.dissociateFoodDiaryEntryFromMeetup(foodDiaryEntryId))
    } yield ()).value

  // Delete any previous association first
  def upsertCardioDiaryEntryForMeetup(
    currentUserId: UUID,
    meetupId: UUID,
    cardioDiaryEntryId: UUID
  ): Future[Either[DomainError, MeetupCardioDiaryEntry]] =
    (for {
      _ <- EitherT[Future, DomainError, Meetup](
        meetupRepository
          .getMeetup(meetupId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Meetup not found!"))))
      )
      _ <- EitherT[Future, DomainError, Seq[MeetupParticipant]](
        participantsRepository
          .getParticipantsForMeetup(meetupId)
          .map { participants =>
            if (participants.exists(_.userId == currentUserId)) Right(participants)
            else Left(EntityNotFoundError("Participant for Meetup not found!"))
          }
      )
      _ <- EitherT.right[DomainError](meetupCardioDiaryEntryRepository.deleteCardioDiaryEntryById(cardioDiaryEntryId))
      entry <- EitherT.right[DomainError](
        meetupCardioDiaryEntryRepository.upsertUserMeetupCardioDiaryEntry(currentUserId, meetupId, cardioDiaryEntryId)
      )
    } yield entry).value

  // Delete any previous association first
  def upsertStrengthDiaryEntryForMeetup(
    currentUserId: UUID,
    meetupId: UUID,
    strengthDiaryEntryId: UUID
  ): Future[Either[DomainError, MeetupStrengthDiaryEntry]] =
    (for {
      _ <- EitherT[Future, DomainError, Meetup](
        meetupRepository
          .getMeetup(meetupId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Meetup not found!"))))
      )
      _ <- EitherT[Future, DomainError, Seq[MeetupParticipant]](
        participantsRepository
          .getParticipantsForMeetup(meetupId)
          .map { participants =>
            if (participants.exists(_.userId == currentUserId)) Right(participants)
            else Left(EntityNotFoundError("Participant for Meetup not found!"))
          }
      )
      _ <- EitherT.right[DomainError](
        meetupStrengthDiaryEntryRepository.deleteStrengthDiaryEntryById(strengthDiaryEntryId)
      )
      entry <- EitherT.right[DomainError](
        meetupStrengthDiaryEntryRepository
          .upsertUserMeetupStrengthDiaryEntry(currentUserId, meetupId, strengthDiaryEntryId)
      )
    } yield entry).value

  // Delete any previous association first
  def upsertFoodDiaryEntryForMeetup(
    currentUserId: UUID,
    meetupId: UUID,
    foodDiaryEntryId: UUID
  ): Future[Either[DomainError, MeetupFoodDiaryEntry]] =
    (for {
      _ <- EitherT[Future, DomainError, Meetup](
        meetupRepository
          .getMeetup(meetupId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Meetup not found!"))))
      )
      _ <- EitherT[Future, DomainError, Seq[MeetupParticipant]](
        participantsRepository
          .getParticipantsForMeetup(meetupId)
          .map { participants =>
            if (participants.exists(_.userId == currentUserId)) Right(participants)
            else Left(EntityNotFoundError("Participant for Meetup not found!"))
          }
      )
      _ <- EitherT.right[DomainError](meetupFoodDiaryEntryRepository.deleteFoodDiaryEntryById(foodDiaryEntryId))
      entry <- EitherT.right[DomainError](
        meetupFoodDiaryEntryRepository
          .upsertUserMeetupFoodDiaryEntry(currentUserId, meetupId, foodDiaryEntryId)
      )
    } yield entry).value

  def deleteCardioDiaryEntryById(diaryEntryId: UUID): Future[Unit] =
    meetupCardioDiaryEntryRepository.deleteCardioDiaryEntryById(diaryEntryId)

  def deleteStrengthDiaryEntryById(diaryEntryId: UUID): Future[Unit] =
    meetupStrengthDiaryEntryRepository.deleteStrengthDiaryEntryById(diaryEntryId)

  def deleteFoodDiaryEntryById(diaryEntryId: UUID): Future[Unit] =
    meetupFoodDiaryEntryRepository.deleteFoodDiaryEntryById(diaryEntryId)

  // Removes all associated meetups
  def removeAssociatedDiaryEntriesForMeetupByUser(currentUserId: UUID, meetupId: UUID): Future[Unit] =
    for {
      cardioDiaryEntries <- meetupCardioDiaryEntryRepository.getUserMeetupCardioDiaryEntries(currentUserId, meetupId)
      strengthDiaryEntries <-
        meetupStrengthDiaryEntryRepository.getUserMeetupStrengthDiaryEntries(currentUserId, meetupId)
      foodDiaryEntries <- meetupFoodDiaryEntryRepository.getUserMeetupFoodDiaryEntries(currentUserId, meetupId)

      deleteCardioFut = Future.sequence(cardioDiaryEntries.map(c => deleteCardioDiaryEntryById(c.cardioEntryId)))
      deleteStrengthFut =
        Future.sequence(strengthDiaryEntries.map(c => deleteStrengthDiaryEntryById(c.strengthEntryId)))
      deleteFoodsFut = Future.sequence(foodDiaryEntries.map(c => deleteFoodDiaryEntryById(c.foodEntryId)))

      deleteUnderlyingCardioFut =
        Future.sequence(cardioDiaryEntries.map(c => diaryService.dissociateCardioDiaryEntryFromMeetup(c.cardioEntryId)))
      deleteUnderlyingStrengthFut = Future.sequence(
        strengthDiaryEntries.map(c => diaryService.dissociateStrengthDiaryEntryFromMeetup(c.strengthEntryId))
      )
      deleteUnderlyingFoodFut =
        Future.sequence(foodDiaryEntries.map(c => diaryService.dissociateFoodDiaryEntryFromMeetup(c.foodEntryId)))

      _ <- deleteCardioFut
      _ <- deleteStrengthFut
      _ <- deleteFoodsFut
      _ <- deleteUnderlyingCardioFut
      _ <- deleteUnderlyingStrengthFut
      _ <- deleteUnderlyingFoodFut
    } yield ()

  /**
    *    Deleting user data is an involved operation... it includes
    *    1. Delete all user created meetups
    *        - Cascade delete will take care of participants, availabilities, decisions, comments
    *        - Cascade delete will also take care of associated food/cardio/strength diary entries
    *    2. Replace all user info showing up in OTHER meetups with static-deleted-user-id
    *        - Meetups where current user is a participant
    *        - Meetups involving current user availabilities
    *        - Meetups involving current user decisions
    *        - Meetup comments made my current user
    *     3. Delete all diary entries associated with a meetup belonging to current user
    *        - No need to replace with static user
    */
  def deleteAllMeetupDataForUser(userId: UUID): Future[Unit] = {
    for {
      // 1. First we delete all meetups
      _ <- Future.unit
      deleteMeetupsFut = meetupRepository.deleteAllMeetupsCreatedByUser(userId)

      // 2. Replace all user info showing up in other meetups with static deleted user
      staticDeletedUserId = UUID.fromString(settingsService.staticDeletedUserId)
      r1Fut = participantsRepository.replaceParticipantWithDeletedUserId(userId, staticDeletedUserId)
      r2Fut = availabilityRepository.replaceParticipantWithDeletedUserId(userId, staticDeletedUserId)
      r3Fut = decisionsRepository.replaceParticipantWithDeletedUserId(userId, staticDeletedUserId)
      r4Fut = commentsRepository.replaceParticipantWithDeletedUserId(userId, staticDeletedUserId)

      // 3. Delete all diary entries associated with a meetup belonging to current user
      cardioDiaryEntriesFut = meetupCardioDiaryEntryRepository.deleteAllUserCardioDiaryEntries(userId)
      strengthDiaryEntriesFut = meetupStrengthDiaryEntryRepository.deleteAllUserStrengthDiaryEntries(userId)
      foodDiaryEntriesFut = meetupFoodDiaryEntryRepository.deleteAllUserFoodDiaryEntries(userId)

      _ <- deleteMeetupsFut
      _ <- r1Fut
      _ <- r2Fut
      _ <- r3Fut
      _ <- r4Fut
      _ <- cardioDiaryEntriesFut
      _ <- strengthDiaryEntriesFut
      _ <- foodDiaryEntriesFut
    } yield ()
  }

}
