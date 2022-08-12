package land.vani.plugin.core.features

import com.onarandombox.MultiverseCore.MultiverseCore
import land.vani.mcorouhlin.paper.event.on
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.ResetWorldConfig
import land.vani.plugin.core.util.joinToComponent
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.event.player.PlayerChangedWorldEvent
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
            if (event.player.location.world in resetWorldConfig.resetWorlds.map { it.world }) {
                resetWorldConfig.playersLogoutAtResetWorld += event.player
            } else {
                resetWorldConfig.playersLogoutAtResetWorld -= event.player
            }
        }

        on<PlayerChangedWorldEvent> { event ->
            val player = event.player
            val world = player.world
            val resetWorldConfigNode = resetWorldConfig.resetWorlds
                .find { it.world == world } ?: return@on

            player.showTitle(
                Title.title(
                    text {
                        content("リセットワールド")
                        color(NamedTextColor.YELLOW)
                    },
                    resetWorldConfigNode.displayName
                )
            )
            sendMessage(
                player,
                Component.newline() + text {
                    content("このワールドはリセットワールドです.")
                } + Component.newline() + text {
                    content("以下の特殊ルールが適用されます.")
                } + Component.newline() + resetWorldConfigNode.specialRules.joinToComponent()
            )
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

        resetWorldConfig.resetWorlds.forEach { resetWorldNode ->
            multiverseCore.mvWorldManager.regenWorld(resetWorldNode.world.name, true, true, null)
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
