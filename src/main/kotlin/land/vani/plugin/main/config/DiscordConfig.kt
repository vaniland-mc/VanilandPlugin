package land.vani.plugin.main.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType

class DiscordConfig(
    private val inner: CustomConfig,
) {
    val token: String
        get() = inner.get("token", ConfigDataType.String)!!
    val guildId: String
        get() = inner.get("guildId", ConfigDataType.String)!!

    val banManagerNotifyChannel: String
        get() = inner.get("banManagerChannel", ConfigDataType.String)!!

    val mcBansNotifyChannel: String
        get() = inner.get("mcBansChannel", ConfigDataType.String)!!
}
