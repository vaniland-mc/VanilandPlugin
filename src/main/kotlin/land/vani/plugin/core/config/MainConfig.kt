package land.vani.plugin.core.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.features.Feature
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.io.path.div
import kotlin.io.path.reader

class MainConfig(private val plugin: VanilandPlugin) {
    private val configPath = plugin.dataFolder.toPath() / "config.yml"
    private val config = YamlConfiguration()

    suspend fun reload() {
        withContext(Dispatchers.IO) {
            config.load(configPath.reader())
        }
    }

    val features: List<Feature.Key<*>>
        get() = config.getConfigurationSection("features")?.getValues(false)
            .orEmpty()
            .map { (key, value) -> Feature.Key(key) to value.toString().toBooleanStrict() }
            .filter { (_, value) -> value }
            .map { (key, _) -> key }

    val voteBlacklistedWorlds: List<World>
        get() = config.getStringList("voteBlacklistedWorlds").mapNotNull { worldName ->
            plugin.server.getWorld(worldName).also {
                if (it == null) {
                    plugin.slF4JLogger.warn(
                        "[config.yml] World '$worldName' is set in voteBlacklistedWorlds but that world is not found"
                    )
                }
            }
        }

    val voteBonusAwaitingPlayers: MutableMap<String, Int>
        get() = config.getConfigurationSection("voteBonusAwaitingPlayers")?.getValues(false)
            .orEmpty()
            .mapValues { (_, value) -> value.toString().toInt() }
            .toMutableMap()
            .let { delegate ->
                ObservableMap(
                    delegate,
                    putCallback = { key, value ->
                        config["voteBonusAwaitingPlayers.$key"] = value
                    },
                    removeCallback = { key ->
                        config["voteBonusAwaitingPlayers.$key"] = null
                    },
                    clearCallback = {
                        config["voteBonusAwaitingPlayers"] = null
                    }
                )
            }
}
