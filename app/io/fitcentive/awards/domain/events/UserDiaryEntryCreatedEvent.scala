package io.fitcentive.awards.domain.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

import java.util.UUID

case class UserDiaryEntryCreatedEvent(userId: UUID, date: String, activityMinutes: Option[Int]) extends EventMessage

object UserDiaryEntryCreatedEvent extends PubSubOps {

  implicit val codec: Codec[UserDiaryEntryCreatedEvent] =
    deriveCodec[UserDiaryEntryCreatedEvent]

  implicit val converter: PubSubMessageConverter[UserDiaryEntryCreatedEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[UserDiaryEntryCreatedEvent]
}
