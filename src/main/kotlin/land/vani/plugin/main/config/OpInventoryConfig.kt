package land.vani.plugin.main.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.item.itemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory
import java.util.UUID

class OpInventoryConfig(
    private val inner: CustomConfig,
) {
    fun applyOpInventoryToPlayer(player: Player) {
        applyInventory(player, "op")
    }

    fun setOpInventory(player: Player, inventory: PlayerInventory) {
        setInventory(player.uniqueId, inventory, "op")
    }

    fun applyPlayerInventoryToPlayer(player: Player) {
        applyInventory(player, "player")
    }

    fun setPlayerInventory(player: Player, inventory: PlayerInventory) {
        setInventory(player.uniqueId, inventory, "player")
    }

    private fun setInventory(uuid: UUID, inventory: PlayerInventory, prefix: String) {
        inventory.armorContents.forEachIndexed { index, itemStack ->
            inner.set("$prefix.$uuid.armor.$index", ConfigDataType.SerializableItemStack, itemStack)
        }
        inventory.storageContents.forEachIndexed { index, itemStack ->
            inner.set("$prefix.$uuid.storage.$index", ConfigDataType.SerializableItemStack, itemStack)
        }
        inventory.extraContents.forEachIndexed { index, itemStack ->
            inner.set("$prefix.$uuid.extra.$index", ConfigDataType.SerializableItemStack, itemStack)
        }
        inner.save()
    }

    private fun applyInventory(player: Player, prefix: String) {
        val armorContents = Array(player.inventory.armorContents.size) {
            inner.get("$prefix.${player.uniqueId}.armor.$it", ConfigDataType.SerializableItemStack, false)
        }
        val storageContents = Array(player.inventory.storageContents.size) {
            inner.get("$prefix.${player.uniqueId}.storage.$it", ConfigDataType.SerializableItemStack, false)
                ?: itemStack(Material.AIR)
        }
        val extraContents = Array(player.inventory.extraContents.size) {
            inner.get("$prefix.${player.uniqueId}.extra.$it", ConfigDataType.SerializableItemStack, false)
        }

        player.inventory.apply {
            setArmorContents(armorContents)
            setStorageContents(storageContents)
            setExtraContents(extraContents)
        }
        player.updateInventory()
    }

    fun reload() = inner.reload()
}
