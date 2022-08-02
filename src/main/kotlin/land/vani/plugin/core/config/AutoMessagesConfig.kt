package land.vani.plugin.core.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.io.path.div
import kotlin.io.path.reader
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class AutoMessagesConfig(
    plugin: VanilandPlugin,
    private val miniMessage: MiniMessage,
) {
    private val configPath = plugin.dataFolder.toPath() / "autoMessages.yml"
    private val config = YamlConfiguration()

    suspend fun reload() {
        withContext(Dispatchers.IO) {
            config.load(configPath.reader())
        }
    }

    val period: Duration
        get() = config.getInt("period").minutes

    val prefix: Component
        get() = config.getString("prefix")?.let { miniMessage.deserialize(it) }
            ?: Component.empty()

    val messages: List<Component>
        get() = config.getStringList("messages").map { miniMessage.deserialize(it) }
}
