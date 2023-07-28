package io.fitcentive.awards.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubSubscriptionConfig

case class SubscriptionsConfig(
  scheduledMeetupReminderSubscription: String,
  scheduledMeetupStateTransitionSubscription: String
) extends PubSubSubscriptionConfig {

  val subscriptions: Seq[String] = Seq(scheduledMeetupReminderSubscription, scheduledMeetupStateTransitionSubscription)
}

object SubscriptionsConfig {
  def fromConfig(config: Config): SubscriptionsConfig =
    SubscriptionsConfig(
      config.getString("scheduled-meetup-reminder"),
      config.getString("scheduled-meetup-state-transition"),
    )
}
