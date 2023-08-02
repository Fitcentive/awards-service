package io.fitcentive.awards.infrastructure.utils

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

trait StreakSupport {
  def calculateStreak(distinctDateStrings: Seq[String], offsetInMinutes: Int = 0): Int = {
    def calculateInternal(currentIndex: Int): Int = {
      if (currentIndex >= distinctDateStrings.length) 0
      else {
        val currentDay = LocalDateTime
          .ofInstant(Instant.now(), ZoneOffset.UTC)
          .plus(-offsetInMinutes, ChronoUnit.MINUTES)
          .minus(currentIndex, ChronoUnit.DAYS)
          .format(DateTimeFormatter.ISO_LOCAL_DATE)

        if (distinctDateStrings(currentIndex) == currentDay) 1 + calculateInternal(currentIndex + 1)
        else 0
      }
    }
    calculateInternal(0)
  }
}
