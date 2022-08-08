package land.vani.plugin.core.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.util.UUID
import kotlin.io.path.div
import kotlin.io.path.reader
import kotlin.io.path.writeText

class OpInventoryConfig(
    plugin: VanilandPlugin,
) {
    private val configPath = plugin.dataFolder.toPath() / "opInventory.yml"
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

    val playerInventories: MutableMap<UUID, Array<ItemStack?>>
        get() = config.getConfigurationSection("player")?.getValues(true).orEmpty()
            .map { (key, value) ->
                @Suppress("UNCHECKED_CAST")
                UUID.fromString(key) to (value as List<ItemStack?>).toTypedArray()
            }.toMap(mutableMapOf())
            .let { delegate ->
                ObservableMap(
                    delegate,
                    putCallback = { key, value ->
                        config.set("player.$key", value.toList())
                        runBlocking {
                            save()
                        }
                    },
                    removeCallback = { key ->
                        config.set("player.$key", null)
                        runBlocking {
                            save()
                        }
                    },
                    clearCallback = {
                        config.set("player", null)
                        runBlocking {
                            save()
                        }
                    }
                )
            }

    val opInventories: MutableMap<UUID, Array<ItemStack?>>
        get() = config.getConfigurationSection("op")?.getValues(true).orEmpty()
            .map { (key, value) ->
                @Suppress("UNCHECKED_CAST")
                UUID.fromString(key) to (value as List<ItemStack?>).toTypedArray()
            }.toMap(mutableMapOf())
            .let { delegate ->
                ObservableMap(
                    delegate,
                    putCallback = { key, value ->
                        config.set("op.$key", value.toList())
                        runBlocking {
                            save()
                        }
                    },
                    removeCallback = { key ->
                        config.set("op.$key", null)
                        runBlocking {
                            save()
                        }
                    },
                    clearCallback = {
                        config.set("op", null)
                        runBlocking {
                            save()
                        }
                    }
                )
            }
}
