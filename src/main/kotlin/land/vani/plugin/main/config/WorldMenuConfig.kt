package land.vani.plugin.main.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.item.itemStack
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

class WorldMenuConfig(
    private val inner: CustomConfig,
) {
    val presets: Map<String, List<WorldMenuDetails>>
        get() = inner.run {
            section("presets")!!.associate { presetName ->
                val details = section("presets.$presetName")!!.map { worldId ->
                    val slot = get("${getPath(presetName, worldId)}.slot", ConfigDataType.Int)!!
                    val material = get("${getPath(presetName, worldId)}.material", ConfigDataType.Material)!!
                    val displayName = get("${getPath(presetName, worldId)}.displayName", ConfigDataType.String)!!
                    val lore = get("${getPath(presetName, worldId)}.lore", ConfigDataType.StringList)!!
                    val teleportLocation = get("${getPath(presetName, worldId)}.location", ConfigDataType.Location)!!
                    WorldMenuDetails(slot, itemStack(material, displayName, lore), teleportLocation)
                }
                presetName.lowercase() to details
            }
        }

    fun reload() = inner.reload()
}

data class WorldMenuDetails(
    val slot: Int,
    val itemStack: ItemStack,
    val teleportLocation: Location,
)

private fun getPath(presetName: String, worldId: String) = "presets.$presetName.$worldId"
