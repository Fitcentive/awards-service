package io.fitcentive.awards.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubTopicConfig

case class TopicsConfig(
  participantAddedAvailabilityToMeetupTopic: String,
  participantAddedToMeetupTopic: String,
  meetupDecisionTopic: String,
  meetupReminderTopic: String,
  meetupLocationChangedTopic: String,
  scheduleMeetupReminderForLaterTopic: String,
  cancelScheduledMeetupReminderForLaterTopic: String,
  scheduledMeetupReminderTopic: String,
  scheduleMeetupStateTransitionForLaterTopic: String,
  cancelScheduledMeetupStateTransitionForLaterTopic: String,
  scheduledMeetupStateTransitionTopic: String
) extends PubSubTopicConfig {

  val topics: Seq[String] =
    Seq(
      participantAddedToMeetupTopic,
      meetupDecisionTopic,
      meetupReminderTopic,
      meetupLocationChangedTopic,
      scheduleMeetupReminderForLaterTopic,
      cancelScheduledMeetupReminderForLaterTopic,
      scheduledMeetupReminderTopic,
      participantAddedAvailabilityToMeetupTopic,
      scheduleMeetupStateTransitionForLaterTopic,
      cancelScheduledMeetupStateTransitionForLaterTopic,
      scheduledMeetupStateTransitionTopic,
    )

}

object TopicsConfig {
  def fromConfig(config: Config): TopicsConfig =
    TopicsConfig(
      config.getString("participant-added-availability-to-meetup"),
      config.getString("participant-added-to-meetup"),
      config.getString("meetup-decision"),
      config.getString("meetup-reminder"),
      config.getString("meetup-location-changed"),
      config.getString("schedule-meetup-reminder-for-later"),
      config.getString("cancel-scheduled-meetup-reminder-for-later"),
      config.getString("scheduled-meetup-reminder"),
      config.getString("schedule-meetup-state-transition-for-later"),
      config.getString("cancel-scheduled-meetup-state-transition-for-later"),
      config.getString("scheduled-meetup-state-transition"),
    )
}
