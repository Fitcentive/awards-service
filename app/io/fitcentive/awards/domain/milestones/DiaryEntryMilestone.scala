package io.fitcentive.awards.domain.milestones

import play.api.libs.json.{JsString, Writes}

trait DiaryEntryMilestone extends Milestone {
  override def category: MetricCategory = MetricCategory.DiaryEntryData
}

object DiaryEntryMilestone {

  val diaryEntryMilestonesInOrder: Seq[DiaryEntryMilestone] = List(
    TenEntries,
    FiftyEntries,
    HundredEntries,
    TwoHundredFiftyEntries,
    FiveHundredEntries,
    ThousandEntries,
    TwoThousandEntries,
    FiveThousandEntries,
    TenThousandEntries,
    TwentyFiveThousandEntries,
  )

  def apply(status: String): DiaryEntryMilestone =
    status match {
      case TenEntries.stringValue                => TenEntries
      case FiftyEntries.stringValue              => FiftyEntries
      case HundredEntries.stringValue            => HundredEntries
      case TwoHundredFiftyEntries.stringValue    => TwoHundredFiftyEntries
      case FiveHundredEntries.stringValue        => FiveHundredEntries
      case ThousandEntries.stringValue           => ThousandEntries
      case TwoThousandEntries.stringValue        => TwoThousandEntries
      case FiveThousandEntries.stringValue       => FiveThousandEntries
      case TenThousandEntries.stringValue        => TenThousandEntries
      case TwentyFiveThousandEntries.stringValue => TwentyFiveThousandEntries

      case _ => throw new Exception("Unexpected diary entry milestone")
    }

  implicit lazy val writes: Writes[DiaryEntryMilestone] = {
    {
      case TenEntries                => JsString(TenEntries.stringValue)
      case FiftyEntries              => JsString(FiftyEntries.stringValue)
      case HundredEntries            => JsString(HundredEntries.stringValue)
      case TwoHundredFiftyEntries    => JsString(TwoHundredFiftyEntries.stringValue)
      case FiveHundredEntries        => JsString(FiveHundredEntries.stringValue)
      case ThousandEntries           => JsString(ThousandEntries.stringValue)
      case TwoThousandEntries        => JsString(TwoThousandEntries.stringValue)
      case FiveThousandEntries       => JsString(FiveThousandEntries.stringValue)
      case TenThousandEntries        => JsString(TenThousandEntries.stringValue)
      case TwentyFiveThousandEntries => JsString(TwentyFiveThousandEntries.stringValue)

    }
  }

  case object TenEntries extends DiaryEntryMilestone {
    val stringValue = "TenEntries"
  }
  case object FiftyEntries extends DiaryEntryMilestone {
    val stringValue = "FiftyEntries"
  }
  case object HundredEntries extends DiaryEntryMilestone {
    val stringValue = "HundredEntries"
  }
  case object TwoHundredFiftyEntries extends DiaryEntryMilestone {
    val stringValue = "TwoHundredFiftyEntries"
  }
  case object FiveHundredEntries extends DiaryEntryMilestone {
    val stringValue = "FiveHundredEntries"
  }
  case object ThousandEntries extends DiaryEntryMilestone {
    val stringValue = "ThousandEntries"
  }
  case object TwoThousandEntries extends DiaryEntryMilestone {
    val stringValue = "TwoThousandEntries"
  }
  case object FiveThousandEntries extends DiaryEntryMilestone {
    val stringValue = "FiveThousandEntries"
  }
  case object TenThousandEntries extends DiaryEntryMilestone {
    val stringValue = "TenThousandEntries"
  }
  case object TwentyFiveThousandEntries extends DiaryEntryMilestone {
    val stringValue = "TwentyFiveThousandEntries"
  }

}
