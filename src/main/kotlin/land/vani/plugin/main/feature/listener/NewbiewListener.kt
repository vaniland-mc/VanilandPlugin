package land.vani.plugin.main.feature.listener

import com.github.syari.spigot.api.event.Events
import com.github.syari.spigot.api.item.itemStack
import land.vani.plugin.main.util.giveItemOrDrop
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import java.time.Duration

private const val BREAD_AMOUNT = 32

private val NEWBIE_ITEMS = setOf(
    itemStack(Material.STONE_PICKAXE),
    itemStack(Material.STONE_SWORD),
    itemStack(Material.STONE_AXE),
    itemStack(Material.STONE_SHOVEL),
    itemStack(Material.BREAD) { amount = BREAD_AMOUNT }
)

private const val TITLE_FADE_IN = 500L
private const val TITLE_STAY = 3000L
private const val TITLE_FADE_OUT = 500L

fun Events.registerNewbieListener() {
    event<PlayerJoinEvent> { event ->
        if (event.player.hasPlayedBefore()) return@event

        event.player.giveItemOrDrop(NEWBIE_ITEMS)

        plugin.server.broadcast(text {
            content("[お初]")
            color(NamedTextColor.YELLOW)
        } + text {
            content("${event.player.name}さんが初上陸しました!")
            color(NamedTextColor.AQUA)
        })

        event.player.run {
            showTitle(Title.title(
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
                Title.Times.of(
                    Duration.ofMillis(TITLE_FADE_IN),
                    Duration.ofMillis(TITLE_STAY),
                    Duration.ofMillis(TITLE_FADE_OUT)
                )
            ))
            sendMessage(text {
                content("バニランドへようこそ!")
                color(NamedTextColor.YELLOW)
                decoration(TextDecoration.BOLD, true)
            })
            sendMessage(text {
                content("はじめてで何をしたらいいかわからない場合は")
                color(NamedTextColor.YELLOW)
            } + text {
                content("公式サイトのFAQ")
                hoverEvent(text {
                    content("クリックでFAQを開く")
                    color(NamedTextColor.WHITE)
                })
                clickEvent(ClickEvent.openUrl("https://vani.land/faq/"))
            } + text {
                content("を参考にしてください!")
            })
        }
    }
}
