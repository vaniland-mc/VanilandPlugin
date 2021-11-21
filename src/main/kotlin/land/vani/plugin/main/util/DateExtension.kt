package land.vani.plugin.main.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

fun LocalDateTime.toDate(): Date =
    Date.from(toInstant(ZoneOffset.of("Asia/Tokyo")))
