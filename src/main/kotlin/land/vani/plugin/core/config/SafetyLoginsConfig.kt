package land.vani.plugin.core.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.util.UUID
import kotlin.io.path.div
import kotlin.io.path.reader
import kotlin.io.path.writeText

class SafetyLoginsConfig(plugin: VanilandPlugin) {
    private val configPath = plugin.dataFolder.toPath() / "safetyLogins.yml"
    private val config = YamlConfiguration()

    suspend fun reload() {
        withContext(Dispatchers.IO) {
            config.load(configPath.reader())
        }
    }

    suspend fun save() {
        withContext(Dispatchers.IO) {
            config.saveToString().let { content ->
                configPath.writeText(content)
            }
        }
    }

    val safetyLogins: MutableMap<UUID, SafetyLoginConfigNode>
        get() = config.getConfigurationSection("safetyLogins")
            ?.getKeys(false)
            ?.mapNotNull { it to config.getConfigurationSection("safetyLogins.$it")!! }
            .orEmpty()
            .associateTo(mutableMapOf()) { (uuid, section) ->
                val player = UUID.fromString(uuid)
                val location = section["location"] as Location
                val reason = section["reason"] as String

                player to SafetyLoginConfigNode(location, reason)
            }.let { delegate ->
                ObservableMap(
                    delegate,
                    putCallback = { key, value ->
                        config["safetyLogins.$key"] = mapOf(
                            "location" to value.location,
                            "reason" to value.reason,
                        )
                        runBlocking {
                            save()
                        }
                    },
                    removeCallback = { key ->
                        config["safetyLogins.$key"] = null
                        runBlocking {
                            save()
                        }
                    },
                    clearCallback = {
                        config["safetyLogins"] = null
                        runBlocking {
                            save()
                        }
                    }
                )
            }
}

data class SafetyLoginConfigNode(
    val location: Location,
    val reason: String,
)
