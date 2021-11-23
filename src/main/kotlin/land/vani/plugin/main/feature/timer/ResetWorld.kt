package land.vani.plugin.main.feature.timer

import com.github.syari.spigot.api.event.Events
import com.onarandombox.MultiverseCore.MultiverseCore
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.config.ResetWorldConfig
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.get
import java.time.LocalDate

fun VanilandPlugin.registerResetWorld() {
    val config = get<ResetWorldConfig>()
    val mvCore = get<MultiverseCore>()

    if (LocalDate.now().dayOfMonth != 1) {
        if (config.regenerated) {
            config.regenerated = false
        } else {
            return
        }
    }
    if (config.regenerated) return

    server.setWhitelist(true)

    config.resetWorlds.forEach { resetWorld ->
        mvCore.mvWorldManager.regenWorld(resetWorld, true, true, null)
    }

    config.regenerated = true
    config.clearTeleportedList()
    server.setWhitelist(false)
}

fun Events.registerResetWorldSafetySpawn(
    config: ResetWorldConfig,
) {
    event<PlayerJoinEvent> { event ->
        val player = event.player
        if (player.world.name !in config.resetWorlds) {
            return@event
        }
        if (config.isTeleported(player.uniqueId)) {
            return@event
        }
        player.teleport(config.spawnLocation)
        player.sendMessage(text {
            content("リセットワールドが再生成されたため、ロビーにテレポートしました")
            color(NamedTextColor.YELLOW)
        })
        config.setTeleported(player.uniqueId, true)
    }
}
