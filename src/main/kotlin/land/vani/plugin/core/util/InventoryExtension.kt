package land.vani.plugin.core.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.giveItemOrDrop(vararg items: ItemStack) = giveItemOrDrop(items.toList())

fun Player.giveItemOrDrop(items: Collection<ItemStack>) {
    val overedItems = inventory.addItem(*items.toTypedArray()).values
    overedItems.forEach {
        world.dropItemNaturally(location, it)
    }
}
