package land.vani.plugin.core.features

import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class AutoRestart(
    private val plugin: VanilandPlugin,
) : Feature<AutoRestart>() {
    companion object : Key<AutoRestart>("autoRestart") {
        private val RESTART_TIME = LocalTime.of(4, 0, 0)
    }

    override val key: Key<AutoRestart> = Companion

    @Suppress("MagicNumber")
    override suspend fun onEnable() {
        var seconds = 60
        Timer().schedule(
            LocalDate.now().plusDays(1).atTime(RESTART_TIME)
                .atZone(ZoneId.of("Asia/Tokyo"))
                .toInstant().let { Date.from(it) },
            TimeUnit.SECONDS.toMicros(1)
        ) {
            when (seconds) {
                0 -> plugin.server.shutdown()
                in 10..60 step 10 -> broadcast("$seconds 病後にサーバーが定時再起動します")
                in 1..10 -> broadcast("$seconds 病後にサーバーが定時再起動します")
            }
            seconds--
        }
    }

    private fun broadcast(message: String) {
        plugin.server.broadcast(
            text {
                content(message)
                color(NamedTextColor.YELLOW)
            }
        )
    }
}
