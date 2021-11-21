package land.vani.plugin.main.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

private const val JST_OFFSET = 9

fun LocalDateTime.toDate(): Date =
    Date.from(toInstant(ZoneOffset.ofHours(JST_OFFSET)))
