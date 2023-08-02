package io.fitcentive.awards.domain.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

import java.util.UUID

case class UserWeightUpdatedEvent(userId: UUID, date: String, newWeightInLbs: Double) extends EventMessage

object UserWeightUpdatedEvent extends PubSubOps {

  implicit val codec: Codec[UserWeightUpdatedEvent] =
    deriveCodec[UserWeightUpdatedEvent]

  implicit val converter: PubSubMessageConverter[UserWeightUpdatedEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[UserWeightUpdatedEvent]
}
