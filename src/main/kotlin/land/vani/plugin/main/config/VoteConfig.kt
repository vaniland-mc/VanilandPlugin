package land.vani.plugin.main.config

import com.github.syari.spigot.api.config.CustomConfig
import com.github.syari.spigot.api.config.type.ConfigDataType
import org.bukkit.World

class VoteConfig(
    private val inner: CustomConfig,
) {
    val blackListWorlds: List<World>
        get() = inner.get("blackListWorlds", ConfigDataType.WorldList)!!

    val votedPlayers: Map<String, Int>
        get() = inner.section("votedPlayers")!!.map {
            it to inner.get("votedPlayers.$it", ConfigDataType.Int)!!
        }.toMap()

    fun add(playerName: String): Int {
        val previous = inner.get("votedPlayers.$playerName", ConfigDataType.Int, false) ?: 0
        val value = previous + 1
        inner.set("votedPlayers.$playerName", ConfigDataType.Int, value, true)
        return value
    }

    fun remove(playerName: String): Int? = inner.get("votedPlayers.$playerName", ConfigDataType.Int, false)
        .also {
            if (it == null) return@also
            inner.setNull("votedPlayers.$playerName", true)
        }

    fun reload() = inner.reload()
}
