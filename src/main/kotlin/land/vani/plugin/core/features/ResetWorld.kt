package land.vani.plugin.core.features

import com.onarandombox.MultiverseCore.MultiverseCore
import land.vani.mcorouhlin.paper.event.on
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.ResetWorldConfig
import org.bukkit.event.player.PlayerQuitEvent
import java.time.LocalDate

class ResetWorld(
    private val plugin: VanilandPlugin,
    private val resetWorldConfig: ResetWorldConfig,
    private val multiverseCore: MultiverseCore,
) : Feature<ResetWorld>() {
    companion object : Key<ResetWorld>("resetWorld")

    override val key: Key<ResetWorld> = Companion

    override suspend fun onEnable() {
        registerListeners()
        resetIfNotRegenerated()
    }

    private fun registerListeners() = plugin.events {
        on<PlayerQuitEvent> { event ->
            if (event.player.location.world in resetWorldConfig.resetWorlds) {
                resetWorldConfig.playersLogoutAtResetWorld += event.player
            } else {
                resetWorldConfig.playersLogoutAtResetWorld -= event.player
            }
        }
    }

    private suspend fun resetIfNotRegenerated() {
        if (LocalDate.now().dayOfMonth != 1) {
            resetWorldConfig.isRegenerated = false
            resetWorldConfig.save()
            return
        }
        if (resetWorldConfig.isRegenerated) {
            return
        }
        plugin.server.setWhitelist(true)

        resetWorldConfig.resetWorlds.forEach { world ->
            multiverseCore.mvWorldManager.regenWorld(world.name, true, true, null)
        }

        resetWorldConfig.isRegenerated = true
        resetWorldConfig.save()
        plugin.server.setWhitelist(false)

        val safetySpawn = plugin.featuresRegistry.getFeature(SafetyLogin) ?: return
        resetWorldConfig.playersLogoutAtResetWorld.forEach { player ->
            safetySpawn.setSafetyLogin(player, resetWorldConfig.spawnLocation, "リセットワールドが再生成されたため")
        }
    }
}
