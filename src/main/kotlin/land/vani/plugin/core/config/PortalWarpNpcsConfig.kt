package land.vani.plugin.core.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.io.path.div
import kotlin.io.path.reader
import kotlin.io.path.writeText

class PortalWarpNpcsConfig(private val plugin: VanilandPlugin) {
    private val configPath = plugin.dataFolder.toPath() / "portalWarpNpcs.yml"
    private val config = YamlConfiguration()

    suspend fun reload() {
        withContext(Dispatchers.IO) {
            config.load(configPath.reader())
        }
    }

    val warpPortals: Map<World, NpcConfigNode>
        get() = config.getMapList("warpPortals")
            .mapNotNull { map ->
                val world = (map["world"] as? String)?.let { plugin.server.getWorld(it) }
                val spawnLocation = map["spawnLocation"] as? Location
                val portalALocation = map["portalALocation"] as? Location
                val portalBLocation = map["portalBLocation"] as? Location

                if (null in listOf(world, spawnLocation, portalALocation, portalBLocation)) {
                    plugin.slF4JLogger.warn("[warpPortalConfig.yml] world '$world' is set but some properties was null")
                    null
                } else {
                    world!! to NpcConfigNode(spawnLocation!!, portalALocation!!, portalBLocation!!)
                }
            }.toMap(mutableMapOf())
            .let { delegate ->
                ObservableMap(
                    delegate,
                    putCallback = { _, _ ->
                        setToConfig(delegate)
                        configPath.writeText(config.saveToString())
                    },
                    removeCallback = {
                        setToConfig(delegate)
                        configPath.writeText(config.saveToString())
                    },
                    clearCallback = {
                        setToConfig(delegate)
                        configPath.writeText(config.saveToString())
                    }
                )
            }

    private fun setToConfig(value: Map<World, NpcConfigNode>) {
        config.set(
            "warpPortals",
            value.map { (key, value) ->
                mapOf(
                    "world" to key.name,
                    "spawnLocation" to value.spawnLocation,
                    "portalALocation" to value.portalALocation,
                    "portalBLocation" to value.portalBLocation,
                )
            }
        )
    }
}

data class NpcConfigNode(
    val spawnLocation: Location,
    val portalALocation: Location,
    val portalBLocation: Location,
)
