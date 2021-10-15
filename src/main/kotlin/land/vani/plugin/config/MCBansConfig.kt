package land.vani.plugin.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType
import java.util.UUID

class MCBansConfig(
    private val inner: CustomConfig,
) {
    val apiKey: String
        get() = inner.get("apiKey", ConfigDataType.String)!!

    var ignoredUuidList: List<UUID>
        get() = inner.get("ignored", ConfigDataType.UUIDList)!!
        set(value) = inner.set("ignored", ConfigDataType.UUIDList, value, true)

    fun reload() = inner.reload()
}
