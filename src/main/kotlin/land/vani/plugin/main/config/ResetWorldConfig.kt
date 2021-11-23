package land.vani.plugin.main.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType
import org.bukkit.Location
import java.util.UUID

class ResetWorldConfig(
    private val inner: CustomConfig,
) {
    val resetWorlds: List<String>
        get() = inner.get("resetWorlds", ConfigDataType.StringList).orEmpty()

    val spawnLocation: Location
        get() = inner.get("spawnLocation", ConfigDataType.Location)!!

    var regenerated: Boolean
        get() = inner.get("regenerated", ConfigDataType.Boolean)!!
        set(value) = inner.set("regenerated", ConfigDataType.Boolean, value, true)

    fun clearTeleportedList() = inner.setNull("teleported", true)

    fun isTeleported(uuid: UUID): Boolean =
        inner.get("teleported.$uuid", ConfigDataType.Boolean, notFoundError = false) ?: false

    fun setTeleported(uuid: UUID, teleported: Boolean) =
        inner.set("teleported.$uuid", ConfigDataType.Boolean, teleported)

    fun reload() = inner.reload()
}
