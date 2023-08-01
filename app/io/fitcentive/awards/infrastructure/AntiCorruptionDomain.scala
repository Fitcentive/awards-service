package io.fitcentive.awards.infrastructure

import io.fitcentive.awards.domain.events.{UserDiaryEntryCreatedEvent, UserStepDataUpdatedEvent}
import io.fitcentive.registry.events.diary.UserDiaryEntryCreated
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

}
