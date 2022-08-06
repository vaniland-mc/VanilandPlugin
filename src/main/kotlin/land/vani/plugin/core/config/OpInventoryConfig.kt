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
        get() = config.getValues(false)
            .map { (key, value) ->
                UUID.fromString(key) to buildList {
                    @Suppress("UNCHECKED_CAST")
                    (value as Map<String, ItemStack?>).forEach { (t, u) ->
                        add(t.toInt(), u)
                    }
                }.toTypedArray()
            }.toMap(mutableMapOf())
            .let { delegate ->
                ObservableMap(
                    delegate,
                    putCallback = { key, value ->
                        config.set("player.$key", value)
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
        get() = config.getValues(false)
            .map { (key, value) ->
                UUID.fromString(key) to buildList {
                    @Suppress("UNCHECKED_CAST")
                    (value as Map<String, ItemStack?>).forEach { (t, u) ->
                        add(t.toInt(), u)
                    }
                }.toTypedArray()
            }.toMap(mutableMapOf())
            .let { delegate ->
                ObservableMap(
                    delegate,
                    putCallback = { key, value ->
                        config.set("op.$key", value)
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
