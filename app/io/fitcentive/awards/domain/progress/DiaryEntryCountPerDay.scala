package io.fitcentive.awards.domain.progress

import play.api.libs.json.{Json, Writes}

case class DiaryEntryCountPerDay(metricDate: String, entryCount: Int)

object DiaryEntryCountPerDay {
  implicit lazy val writes: Writes[DiaryEntryCountPerDay] = Json.writes[DiaryEntryCountPerDay]
}
