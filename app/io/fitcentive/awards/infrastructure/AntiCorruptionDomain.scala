package io.fitcentive.awards.infrastructure

import io.fitcentive.awards.domain.events.{UserDiaryEntryCreatedEvent, UserStepDataUpdatedEvent, UserWeightUpdatedEvent}
import io.fitcentive.registry.events.diary.{UserDiaryEntryCreated, UserWeightUpdated}
import io.fitcentive.registry.events.steps.UserStepDataUpdated

trait AntiCorruptionDomain {

  implicit class UserStepDataUpdatedEvent2Domain(event: UserStepDataUpdated) {
    def toDomain: UserStepDataUpdatedEvent =
      UserStepDataUpdatedEvent(event.userId, event.date, event.stepsTaken)
  }

  implicit class UserDiaryEntryCreatedEvent2Domain(event: UserDiaryEntryCreated) {
    def toDomain: UserDiaryEntryCreatedEvent =
      UserDiaryEntryCreatedEvent(event.userId, event.date, event.activityMinutes)
  }

  implicit class UserWeightUpdatedEvent2Domain(event: UserWeightUpdated) {
    def toDomain: UserWeightUpdatedEvent =
      UserWeightUpdatedEvent(event.userId, event.date, event.newWeightInLbs)
  }

}
