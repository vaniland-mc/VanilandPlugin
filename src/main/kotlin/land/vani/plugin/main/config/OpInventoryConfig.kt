package land.vani.plugin.main.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class OpInventoryConfig(
    private val inner: CustomConfig,
) {
    fun getOpInventory(player: Player): Array<ItemStack?>? =
        inner.get("op.${player.uniqueId}", ConfigDataType.SerializableInventory, false)

    fun setOpInventory(player: Player, inventory: Inventory) {
        inner.set("op.${player.uniqueId}", ConfigDataType.SerializableInventory, inventory.contents)
    }

    fun getPlayerInventory(player: Player): Array<ItemStack?>? =
        inner.get("player.${player.uniqueId}", ConfigDataType.SerializableInventory, false)

    fun setPlayerInventory(player: Player, inventory: Inventory) {
        inner.set("player.${player.uniqueId}", ConfigDataType.SerializableInventory, inventory.contents)
    }

    fun reload() = inner.reload()
}
