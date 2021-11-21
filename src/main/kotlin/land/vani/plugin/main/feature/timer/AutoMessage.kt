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
        } + text {
            content("monocraftはこちら")
            color(NamedTextColor.AQUA)
            hoverEvent(text {
                content("クリックでmonocraftの投票ページを開く")
                color(NamedTextColor.GRAY)
            })
            clickEvent(ClickEvent.openUrl("https://monocraft.net/servers/ZhtDlW4bqkRvMzOyxfA6/vote"))
        },

        PREFIX + text {
            content("迷子になったときは")
            color(NamedTextColor.GOLD)
        } + text {
            content("Dynmap")
            color(NamedTextColor.AQUA)
            hoverEvent(text {
                content("クリックでDynmapを開く")
                color(NamedTextColor.GRAY)
            })
            clickEvent(ClickEvent.openUrl("https://maps.vani.land"))
        },

        PREFIX + text {
            content("バニランドには")
            color(NamedTextColor.GOLD)
        } + text {
            content("公式Discordコミュニティ")
            color(NamedTextColor.AQUA)
            hoverEvent(text {
                content("クリックでDiscordの招待ページを表示")
                color(NamedTextColor.GRAY)
            })
            clickEvent(ClickEvent.openUrl("https://discord.gg/gGhPyDQpFN"))
        } + text {
            content("があります!")
            color(NamedTextColor.GOLD)
        },

        PREFIX + text {
            content("注意喚起\n")
            color(NamedTextColor.RED)
        } + text {
            content("X-Rayの使用や荒らし行為は処罰対象です\n")
        } + text {
            content("また、公共植林場や畑を利用したあと植え直しをしないブルドーザー行為も荒らしとみなされる場合があります\n")
        } + text {
            content("みなさんが気持ちよくプレイできるよう協力をおねがいします")
        }
    )
}.flatten()

private const val PERIOD = 5 * 60 * 20L

fun VanilandPlugin.registerAutoMessage() {
    val iterator = MESSAGES.iterator()
    runTaskTimer(PERIOD, async = true) {
        server.broadcast(iterator.next())
    }
}
