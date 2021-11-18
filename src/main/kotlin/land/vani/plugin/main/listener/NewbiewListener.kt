package land.vani.plugin.main.listener

import com.github.syari.spigot.api.event.Events
import com.github.syari.spigot.api.item.itemStack
import land.vani.plugin.main.util.giveItemOrDrop
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent

private const val BREAD_AMOUNT = 32

private val NEWBIE_ITEMS = setOf(
    itemStack(Material.STONE_PICKAXE),
    itemStack(Material.STONE_SWORD),
    itemStack(Material.STONE_AXE),
    itemStack(Material.STONE_SHOVEL),
    itemStack(Material.BREAD) { amount = BREAD_AMOUNT }
)

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
    }
}
