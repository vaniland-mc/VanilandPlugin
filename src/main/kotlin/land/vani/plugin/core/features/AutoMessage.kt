package land.vani.plugin.core.features

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.timer.timerFlow
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import kotlin.time.Duration.Companion.minutes

private val PREFIX = text {
    content("[定期]")
    color(NamedTextColor.DARK_AQUA)
    decoration(TextDecoration.BOLD, true)
} + text {
    resetStyle()
}

private val MESSAGES = generateSequence {
    setOf(
        PREFIX + text {
            resetStyle()
            content("公式ホームページ")
            color(NamedTextColor.GOLD)
            hoverEvent(
                text {
                    content("クリックで公式ホームページを開く")
                    color(NamedTextColor.GRAY)
                }
            )
            clickEvent(ClickEvent.openUrl("https://vani.land"))
        } + text {
            resetStyle()
            content("でルールを確認してください!")
            color(NamedTextColor.GRAY)
        },
        PREFIX + text {
            resetStyle()
            content("サーバーリスト")
            color(NamedTextColor.GOLD)
            hoverEvent(
                text {
                    content("クリックでmonocraftの投票ページを開く")
                    color(NamedTextColor.GRAY)
                }
            )
            clickEvent(ClickEvent.openUrl("https://monocraft.net/servers/ZhtDlW4bqkRvMzOyxfA6/vote"))
        } + text {
            resetStyle()
            content("で投票をお願いします!")
            color(NamedTextColor.GRAY)
        },
        PREFIX + text {
            resetStyle()
            content("迷子になったときは")
            color(NamedTextColor.GRAY)
        } + text {
            resetStyle()
            content("Dynmap")
            color(NamedTextColor.GOLD)
            hoverEvent(
                text {
                    content("クリックでDynmapを開く")
                    color(NamedTextColor.GRAY)
                }
            )
            clickEvent(ClickEvent.openUrl("https://maps.vani.land"))
        } + text {
            resetStyle()
            content("で自分の位置を探そう!")
            color(NamedTextColor.GRAY)
        },
        PREFIX + text {
            resetStyle()
            content("バニランドには")
            color(NamedTextColor.GRAY)
        } + text {
            resetStyle()
            content("公式Discordコミュニティ")
            color(NamedTextColor.GOLD)
            hoverEvent(
                text {
                    content("クリックでDiscordコミュニティの招待を開く")
                    color(NamedTextColor.GRAY)
                }
            )
            clickEvent(ClickEvent.openUrl("https://discord.gg/gGhPyDQpFN"))
        } + text {
            resetStyle()
            content("があります!")
            color(NamedTextColor.GRAY)
        },
        PREFIX + text {
            resetStyle()
            content("注意喚起\n")
            color(NamedTextColor.RED)
            decoration(TextDecoration.BOLD, true)
        } + text {
            resetStyle()
            content("X-Rayの使用や荒らし行為は処罰対象です\n")
            color(NamedTextColor.GRAY)
        } + text {
            resetStyle()
            content("また、公共植林場や畑を利用したあと植え直しをしないブルドーザー行為も荒らしとみなされる場合があります\n")
            color(NamedTextColor.GRAY)
        } + text {
            resetStyle()
            content("荒らしかどうか判断に迷うものの対応は運営が決定するため、運営連絡所を通じて報告をお願いします\n")
            color(NamedTextColor.GRAY)
        } + text {
            content("皆さんが気持ちよくプレイできるようご協力をお願いします")
            color(NamedTextColor.GRAY)
        },
    )
}.flatten()

private val PERIOD = 5.minutes

object AutoMessage : Feature {
    override val key: Feature.Key = Feature.Key("autoMessage")

    private var job: Job? = null

    override suspend fun onEnable(plugin: VanilandPlugin) {
        val iterator = MESSAGES.iterator()
        job?.cancel()
        job = timerFlow(PERIOD).onEach {
            withContext(plugin.mainThreadDispatcher) {
                val message = iterator.next()
                plugin.server.broadcast(message)
            }
        }.launchIn(plugin + plugin.asyncDispatcher)
    }

    override suspend fun onDisable(plugin: VanilandPlugin) {
        job?.cancel()
    }
}
