package land.vani.plugin.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.item.itemStack
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

class WorldMenuConfig(
    private val inner: CustomConfig,
) {
    val worlds: List<WorldMenuDetails>
        get() = inner.run {
            section("worlds")?.map { worldId ->
                val slot = get("worlds.${worldId}.slot", ConfigDataType.Int)!!
                val material = get("worlds.${worldId}.material", ConfigDataType.Material)!!
                val displayName = get("worlds.${worldId}.displayName", ConfigDataType.String)!!
                val lore = get("worlds.${worldId}.lore", ConfigDataType.StringList)!!
                val teleportLocation = get("worlds.${worldId}.location", ConfigDataType.Location)!!
                WorldMenuDetails(slot, itemStack(material, displayName, lore), teleportLocation)
            }!!
        }

    fun reload() = inner.reload()
}

data class WorldMenuDetails(
    val slot: Int,
    val itemStack: ItemStack,
    val teleportLocation: Location,
)
