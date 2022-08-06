package land.vani.plugin.core.features

import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.style
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

abstract class Feature<T : Feature<T>> {
    companion object {
        private val SYSTEM_MESSAGE_PREFIX_STYLE = style {
            color(NamedTextColor.YELLOW)
        }
    }

    abstract val key: Key<T>

    var isEnabled: Boolean = false

    open suspend fun onEnable() {}

    open suspend fun onDisable() {}

    fun sendMessage(sender: CommandSender, message: Component) {
        val formattedMessage = text {
            content("[システム($key)]")
            style(SYSTEM_MESSAGE_PREFIX_STYLE)
        } + message
        sender.sendMessage(formattedMessage)
    }

    override fun toString(): String = "Feature(key=$key, isEnabled=$isEnabled)"

    open class Key<@Suppress("unused") T : Feature<T>>(val key: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Key<*>) return false
            return key == other.key
        }

        override fun hashCode(): Int = key.hashCode()

        override fun toString(): String = key
    }
}
