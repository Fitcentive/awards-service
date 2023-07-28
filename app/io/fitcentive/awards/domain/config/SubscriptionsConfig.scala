package io.fitcentive.awards.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubSubscriptionConfig

case class SubscriptionsConfig(userStepDataUpdatedSubscription: String) extends PubSubSubscriptionConfig {

  val subscriptions: Seq[String] = Seq(userStepDataUpdatedSubscription)
}

object SubscriptionsConfig {
  def fromConfig(config: Config): SubscriptionsConfig =
    SubscriptionsConfig(config.getString("user-step-data-updated"))
}
