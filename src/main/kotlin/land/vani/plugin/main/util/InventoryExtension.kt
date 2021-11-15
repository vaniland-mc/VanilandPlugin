package land.vani.plugin.main.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.giveItemOrDrop(vararg itemStack: ItemStack) {
    val flowedItems = inventory.addItem(*itemStack).values
    flowedItems.forEach {
        world.dropItemNaturally(location, it)
    }
}
