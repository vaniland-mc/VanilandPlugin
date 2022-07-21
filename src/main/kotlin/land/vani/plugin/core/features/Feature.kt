package land.vani.plugin.core.features

import land.vani.plugin.core.VanilandPlugin
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.style
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender

abstract class Feature<T : Feature<T>> {
    companion object {
        private val SYSTEM_MESSAGE_PREFIX_STYLE = style {
            color(NamedTextColor.YELLOW)
            decoration(TextDecoration.BOLD, true)
        }
    }

    abstract val key: Key<T>

    var isEnabled: Boolean = false

    open suspend fun onEnable(plugin: VanilandPlugin) {}

    open suspend fun onDisable(plugin: VanilandPlugin) {}

    fun sendMessage(sender: CommandSender, message: Component) {
        val formattedMessage = text { }.run {
            this + text {
                content("[システム($key)]")
                style(SYSTEM_MESSAGE_PREFIX_STYLE)
            }
            this + message
        }
        sender.sendMessage(formattedMessage)
    }

    @JvmInline
    value class Key<@Suppress("unused") T : Feature<T>>(private val key: String) {
        override fun toString(): String = key
    }
}
