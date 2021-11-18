package land.vani.plugin.main.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.giveItemOrDrop(vararg itemStacks: ItemStack) {
    val flowedItems = inventory.addItem(*itemStacks).values
    flowedItems.forEach {
        world.dropItemNaturally(location, it)
    }
}

fun <T : Collection<ItemStack>> Player.giveItemOrDrop(itemStacks: T) = giveItemOrDrop(*itemStacks.toTypedArray())
