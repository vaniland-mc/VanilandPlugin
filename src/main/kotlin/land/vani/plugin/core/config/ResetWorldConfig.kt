package land.vani.plugin.core.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import java.util.UUID
import kotlin.io.path.div
import kotlin.io.path.reader
import kotlin.io.path.writeText

class ResetWorldConfig(
    private val plugin: VanilandPlugin,
    private val miniMessage: MiniMessage,
) {
    private val configPath = plugin.dataFolder.toPath() / "resetWorlds.yml"
    private val config = YamlConfiguration()

    suspend fun reload() {
        withContext(Dispatchers.IO) {
            config.load(configPath.reader())
        }
    }

    suspend fun save() {
        withContext(Dispatchers.IO) {
            configPath.writeText(config.saveToString())
        }
    }

    val spawnLocation: Location
        get() = config.getLocation("spawnLocation")!!

    val resetWorlds: List<ResetWorldConfigNode>
        get() = config.getMapList("resetWorlds")
            .mapNotNull { map ->
                @Suppress("UNCHECKED_CAST")
                map as Map<String, Any>
                val world = (map["world"]!! as String).let { plugin.server.getWorld(it) }
                    ?: run {
                        plugin.logger.warning(
                            "World '${map["world"]}' is set in resetWorlds.yml but not found in server"
                        )
                        return@mapNotNull null
                    }
                val displayName = (map["displayName"] as String)
                    .let { miniMessage.deserialize(it) }

                @Suppress("UNCHECKED_CAST")
                val specialRules = (map["specialRules"] as List<String>)
                    .map { miniMessage.deserialize(it) }

                ResetWorldConfigNode(world, displayName, specialRules)
            }

    val playersLogoutAtResetWorld: MutableList<OfflinePlayer>
        get() = config.getStringList("playersLogoutAtResetWorld")
            .map {
                plugin.server.getOfflinePlayer(UUID.fromString(it))
            }.toMutableList()
            .let { delegate ->
                ObservableList(
                    delegate,
                    addCallback = {
                        config.set("playersLogoutAtResetWorld", delegate.map { it.uniqueId.toString() })
                        runBlocking {
                            save()
                        }
                    },
                    removeCallback = {
                        config.set("playersLogoutAtResetWorld", delegate.map { it.uniqueId.toString() })
                        runBlocking {
                            save()
                        }
                    }
                )
            }

    var isRegenerated: Boolean
        get() = config.getBoolean("regenerated")
        set(value) = config.set("regenerated", value)
}

data class ResetWorldConfigNode(
    val world: World,
    val displayName: Component,
    val specialRules: List<Component>,
)
