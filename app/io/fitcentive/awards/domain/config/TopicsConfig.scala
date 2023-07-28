package io.fitcentive.awards.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubTopicConfig

case class TopicsConfig(userStepDataUpdatedTopic: String, userAttainedNewAchievementMilestoneTopic: String)
  extends PubSubTopicConfig {

  val topics: Seq[String] =
    Seq(userStepDataUpdatedTopic, userAttainedNewAchievementMilestoneTopic)

}

object TopicsConfig {
  def fromConfig(config: Config): TopicsConfig =
    TopicsConfig(
      config.getString("user-step-data-updated"),
      config.getString("user-attained-new-achievement-milestone"),
    )
}
