package land.vani.plugin.core.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.io.path.div
import kotlin.io.path.reader

class WorldWarpNpcsConfig(
    plugin: VanilandPlugin,
    private val miniMessage: MiniMessage,
) {
    private val configPath = plugin.dataFolder.toPath() / "worldWarpNpcs.yml"
    private val config = YamlConfiguration()

    suspend fun reload() {
        withContext(Dispatchers.IO) {
            config.load(configPath.reader())
        }
    }

    val worlds: List<WorldWarpNode>
        get() = config.getMapList("worlds").map { map ->
            @Suppress("UNCHECKED_CAST")
            map as Map<String, Any>
            @Suppress("UNCHECKED_CAST")
            WorldWarpNode(
                location = map["location"] as Location,
                material = map["material"] as Material,
                displayName = miniMessage.deserialize(map["displayName"] as String),
                lore = (map["lore"] as List<String>).map { miniMessage.deserialize(it) }
            )
        }
}

data class WorldWarpNode(
    val location: Location,
    val material: Material,
    val displayName: Component,
    val lore: List<Component>,
)
