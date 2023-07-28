package io.fitcentive.awards.services

import com.google.inject.ImplementedBy
import io.fitcentive.awards.domain.milestones.UserMilestone
import io.fitcentive.awards.infrastructure.pubsub.EventPublisherService

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[EventPublisherService])
trait MessageBusService {
  def publishUserAttainedNewAchievementMilestoneEvent(milestone: UserMilestone): Future[Unit]
}
