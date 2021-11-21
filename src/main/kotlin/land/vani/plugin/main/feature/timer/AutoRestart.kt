package land.vani.plugin.main.feature.timer

import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.util.toDate
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

private const val COUNTDOWN_START = 5
private var countdown = COUNTDOWN_START

private const val RESTART_HOUR = 4
private val RESTART_TIME = LocalTime.of(RESTART_HOUR, 0)

fun VanilandPlugin.registerAutoRestart() {
    timer(
        startAt = LocalDate.now().plusDays(1).atTime(RESTART_TIME).toDate(),
        period = TimeUnit.MINUTES.toMillis(1)
    ) {
        if (countdown == 0) {
            server.shutdown()
            return@timer
        }

        server.broadcast(text {
            content("$countdown 分後にサーバーが定時再起動します. プレイヤーの皆さんにはログアウトをお願いします...")
            color(NamedTextColor.YELLOW)
        })
        countdown--
    }
}
