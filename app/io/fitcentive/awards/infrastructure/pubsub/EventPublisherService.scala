package io.fitcentive.awards.infrastructure.pubsub

import io.fitcentive.awards.domain.config.TopicsConfig
import io.fitcentive.awards.domain.milestones.UserMilestone
import io.fitcentive.awards.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.awards.services.{MessageBusService, SettingsService}
import io.fitcentive.registry.events.achievements.UserAttainedNewAchievementMilestone
import io.fitcentive.sdk.gcp.pubsub.PubSubPublisher

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class EventPublisherService @Inject() (publisher: PubSubPublisher, settingsService: SettingsService)(implicit
  ec: PubSubExecutionContext
) extends MessageBusService {

  private val publisherConfig: TopicsConfig = settingsService.pubSubConfig.topicsConfig

  override def publishUserAttainedNewAchievementMilestoneEvent(milestone: UserMilestone): Future[Unit] =
    milestone.toOut
      .pipe(publisher.publish(publisherConfig.userAttainedNewAchievementMilestoneTopic, _))

  /*
  Note - we are changing the name and category here to be user friendly, as this info is relayed via push notification title
   */
  implicit class UserAttainedNewAchievementMilestoneToOut(in: UserMilestone) {
    def toOut: UserAttainedNewAchievementMilestone =
      UserAttainedNewAchievementMilestone(
        userId = in.userId,
        milestoneName = in.name.stringValue,
        milestoneCategory = in.milestoneCategory.stringValue,
        attainedAtInMillis = in.createdAt.toEpochMilli
      )
  }
}
