package io.fitcentive.awards.infrastructure

import io.fitcentive.awards.domain.events.UserStepDataUpdatedEvent
import io.fitcentive.registry.events.steps.UserStepDataUpdated

trait AntiCorruptionDomain {

  implicit class UserStepDataUpdatedEvent2Domain(event: UserStepDataUpdated) {
    def toDomain: UserStepDataUpdatedEvent =
      UserStepDataUpdatedEvent(event.userId, event.date, event.stepsTaken)
  }

}
