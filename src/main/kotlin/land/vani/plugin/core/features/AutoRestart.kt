package land.vani.plugin.core.features

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.timer.timerFlow
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

class AutoRestart(
    private val plugin: VanilandPlugin,
) : Feature<AutoRestart>() {
    companion object : Key<AutoRestart>("autoRestart") {
        private val RESTART_TIME = LocalTime.of(4, 0, 0)
    }

    override val key: Key<AutoRestart> = Companion

    @Suppress("MagicNumber")
    override suspend fun onEnable() {
        val datetimeToRestart = LocalDate.now().plusDays(1).atTime(RESTART_TIME)
        var seconds = 60
        timerFlow(
            period = 1.seconds,
            initialDelay = Duration.between(LocalDateTime.now(), datetimeToRestart).toKotlinDuration()
        ).onEach {
            when (seconds) {
                0 -> plugin.server.shutdown()
                in 10..60 step 10 -> broadcast("$seconds 秒後にサーバーが定時再起動します")
                in 1..10 -> broadcast("$seconds 秒後にサーバーが定時再起動します")
            }
            seconds--
        }.launchIn(plugin + Dispatchers.Unconfined)
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
