package land.vani.plugin.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType

class MCBansConfig(
    private val inner: CustomConfig,
    private val configKey: String,
) {
    val apiKey: String
        get() = inner.get(configKey, ConfigDataType.String)!!

    fun reload() = inner.reload()
}
