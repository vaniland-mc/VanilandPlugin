package land.vani.plugin.core.features

import land.vani.plugin.core.VanilandPlugin

interface Feature {
    val key: Key

    suspend fun onEnable(plugin: VanilandPlugin) {}

    suspend fun onDisable(plugin: VanilandPlugin) {}

    @JvmInline
    value class Key(private val key: String) {
        override fun toString(): String = key
    }
}
