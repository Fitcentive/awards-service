package io.fitcentive.awards.domain.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

import java.util.UUID

case class UserStepDataUpdatedEvent(userId: UUID, date: String, stepsTaken: Int) extends EventMessage

object UserStepDataUpdatedEvent extends PubSubOps {

  implicit val codec: Codec[UserStepDataUpdatedEvent] =
    deriveCodec[UserStepDataUpdatedEvent]

  implicit val converter: PubSubMessageConverter[UserStepDataUpdatedEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[UserStepDataUpdatedEvent]
}
