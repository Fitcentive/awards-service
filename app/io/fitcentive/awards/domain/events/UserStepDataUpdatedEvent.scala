package io.fitcentive.awards.domain.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

case class ScheduledMeetupReminderEvent(meetupId: String) extends EventMessage

object ScheduledMeetupReminderEvent extends PubSubOps {

  implicit val codec: Codec[ScheduledMeetupReminderEvent] =
    deriveCodec[ScheduledMeetupReminderEvent]

  implicit val converter: PubSubMessageConverter[ScheduledMeetupReminderEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[ScheduledMeetupReminderEvent]
}
