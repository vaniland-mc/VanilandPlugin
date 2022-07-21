package land.vani.plugin.core.features

import land.vani.mcorouhlin.paper.event.on
import land.vani.mcorouhlin.paper.item.itemStack
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.util.giveItemOrDrop
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object Newbie : Feature<Newbie>() {
    override val key: Key<Newbie> = Key("newbie")

    private const val BREAD_AMOUNT = 32

    private val NEWBIE_ITEMS = setOf(
        itemStack(Material.STONE_PICKAXE),
        itemStack(Material.STONE_SWORD),
        itemStack(Material.STONE_AXE),
        itemStack(Material.STONE_SHOVEL),
        itemStack(Material.BREAD, BREAD_AMOUNT),
    )

    private val TITLE_FADE_IN = 0.5.seconds
    private val TITLE_STAY = 3.seconds
    private val TITLE_FADE_OUT = 0.5.seconds

    private fun registerNewbieBroadcast(plugin: VanilandPlugin) = plugin.events {
        on<PlayerJoinEvent> { event ->
            if (event.player.hasPlayedBefore()) return@on

            plugin.server.broadcast(
                text {
                    content("[お初]")
                    color(NamedTextColor.YELLOW)
                    decoration(TextDecoration.BOLD, true)
                } + text {
                    resetStyle()
                    content("${event.player.name}さんが初上陸しました!")
                    color(NamedTextColor.AQUA)
                }
            )
        }
    }

    private fun registerNewbieGiveItem(plugin: VanilandPlugin) = plugin.events {
        on<PlayerJoinEvent> { event ->
            if (event.player.hasPlayedBefore()) return@on

            event.player.giveItemOrDrop(NEWBIE_ITEMS)
        }
    }

    private fun registerNewbieShowNotice(plugin: VanilandPlugin) = plugin.events {
        on<PlayerJoinEvent> { event ->
            if (event.player.hasPlayedBefore()) return@on

            event.player.showTitle(
                Title.title(
                    text {
                        content("バニランド")
                        color(NamedTextColor.AQUA)
                        decoration(TextDecoration.BOLD, true)
                    } + text {
                        content("へようこそ!")
                        color(NamedTextColor.YELLOW)
                        decoration(TextDecoration.BOLD, false)
                    },
                    text {
                        content("正面の旅先案内人から各ワールドに移動できます")
                    },
                    Title.Times.times(
                        TITLE_FADE_IN.toJavaDuration(),
                        TITLE_STAY.toJavaDuration(),
                        TITLE_FADE_OUT.toJavaDuration(),
                    )
                )
            )
            event.player.sendMessage(
                text {
                    content("バニランドへようこそ!")
                    color(NamedTextColor.YELLOW)
                    decoration(TextDecoration.BOLD, true)
                }
            )
            event.player.sendMessage(
                text {
                    content("はじめてで何をしたらいいかわからない場合は")
                    color(NamedTextColor.YELLOW)
                } + text {
                    content("公式サイトのFAQ")
                    hoverEvent(
                        text {
                            content("クリックでFAQを開く")
                            color(NamedTextColor.WHITE)
                        }
                    )
                    clickEvent(ClickEvent.openUrl("https://vani.land/faq/"))
                } + text {
                    content("を参考にしてください!")
                }
            )
        }
    }

    override suspend fun onEnable(plugin: VanilandPlugin) {
        registerNewbieBroadcast(plugin)
        registerNewbieGiveItem(plugin)
        registerNewbieShowNotice(plugin)
    }
}
