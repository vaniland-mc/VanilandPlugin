package land.vani.plugin.main.feature.timer

import com.github.syari.spigot.api.scheduler.runTaskTimer
import land.vani.plugin.main.VanilandPlugin
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

private val PREFIX = text {
    content("[定期]")
    color(NamedTextColor.DARK_AQUA)
    decorate(TextDecoration.BOLD)
}

private val MESSAGES = generateSequence {
    setOf(
        PREFIX + text {
            content("公式ホームページ")
            color(NamedTextColor.GOLD)
            decorate()
            hoverEvent(text {
                content("クリックで公式ホームページを開く")
                color(NamedTextColor.GRAY)
            })
            clickEvent(ClickEvent.openUrl("https://vani.land"))
        } + text {
            content("でルールを確認してください!")
            color(NamedTextColor.GRAY)
        },

        PREFIX + text {
            content("サーバーリストで投票をお願いします!\n")
            color(NamedTextColor.GOLD)
            decorate()
        } + text {
            content("monocraftはこちら")
            color(NamedTextColor.AQUA)
            hoverEvent(text {
                content("クリックでmonocraftの投票ページを開く")
                color(NamedTextColor.GRAY)
            })
            clickEvent(ClickEvent.openUrl("https://monocraft.net/servers/ZhtDlW4bqkRvMzOyxfA6/vote"))
        }
    )
}.flatten()

private const val PERIOD = 1 * 60 * 20L

fun VanilandPlugin.registerAutoMessage() {
    val iterator = MESSAGES.iterator()
    runTaskTimer(PERIOD, async = true) {
        server.broadcast(iterator.next())
    }
}
