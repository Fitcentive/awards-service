package io.fitcentive.awards.domain.progress

import play.api.libs.json.{Json, Writes}

case class ProgressInsights(
  userWeightProgressInsight: ProgressInsight,
  userDiaryEntryProgressInsight: ProgressInsight,
  userActivityMinutesProgressInsight: ProgressInsight
)

/**
  * Level 0 - red
  * Level 1 - orange
  * Level 2 - green
  */
case class ProgressInsight(insight: String, level: Int)

object ProgressInsight {
  implicit lazy val writes: Writes[ProgressInsight] = Json.writes[ProgressInsight]
}

object ProgressInsights {
  implicit lazy val writes: Writes[ProgressInsights] = Json.writes[ProgressInsights]

  def apply(
    currentWeightEntryStreak: Int,
    userDiaryEntryStreak: Int,
    userActivityMinutesForLast7Days: Int
  ): ProgressInsights = {
    val weightInsight = {
      if (currentWeightEntryStreak == 0) ProgressInsight("You should try to log your weight more frequently", 0)
      else if (currentWeightEntryStreak == 1) ProgressInsight("Your weight logging streak began yesterday", 1)
      else if (currentWeightEntryStreak >= 2 && currentWeightEntryStreak <= 5)
        ProgressInsight(s"You are on a $currentWeightEntryStreak day weight logging streak!", 1)
      else ProgressInsight(s"You are on a $currentWeightEntryStreak day weight logging streak!", 2)
    }

    val diaryEntryInsight = {
      if (userDiaryEntryStreak == 0) ProgressInsight("You should try to log your activity more", 0)
      else if (userDiaryEntryStreak == 1) ProgressInsight("Your diary entry streak began yesterday", 1)
      else if (userDiaryEntryStreak >= 2 && userDiaryEntryStreak <= 5)
        ProgressInsight(s"You are on a $userDiaryEntryStreak day diary entry streak!", 1)
      else ProgressInsight(s"You are on a $userDiaryEntryStreak day diary entry streak!", 2)
    }

    val activityMinutesInsight = {
      if (userActivityMinutesForLast7Days == 0) ProgressInsight("You should try to be more active", 0)
      if (userActivityMinutesForLast7Days > 0 && userActivityMinutesForLast7Days < 210)
        ProgressInsight(s"You have been active for $userActivityMinutesForLast7Days minutes this week", 1)
      else ProgressInsight(s"You have been active for $userActivityMinutesForLast7Days minutes this week", 2)
    }

    new ProgressInsights(
      userWeightProgressInsight = weightInsight,
      userDiaryEntryProgressInsight = diaryEntryInsight,
      userActivityMinutesProgressInsight = activityMinutesInsight,
    )
  }
}
