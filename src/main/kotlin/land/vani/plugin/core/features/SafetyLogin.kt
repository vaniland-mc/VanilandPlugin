package land.vani.plugin.core.features

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import land.vani.mcorouhlin.command.arguments.string
import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.command.arguments.offlinePlayer
import land.vani.mcorouhlin.paper.event.on
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.SafetyLoginConfigNode
import land.vani.plugin.core.util.formattedString
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

object SafetyLogin : Feature<SafetyLogin>() {
    override val key: Key<SafetyLogin> = Key("safetyLogin")

    override suspend fun onEnable(plugin: VanilandPlugin) {
        registerEventListener(plugin)
        registerCommands(plugin)
    }

    private fun registerEventListener(plugin: VanilandPlugin) = plugin.events {
        on<PlayerJoinEvent> { event ->
            val player = event.player
            val safetyLogin = plugin.safetyLoginsConfig.safetyLogins[player.uniqueId] ?: return@on
            event.player.teleport(safetyLogin.location)

            sendMessage(
                player,
                text {
                    content("運営によりログイン時にテレポートされました\n理由: ${safetyLogin.reason}")
                    decoration(TextDecoration.BOLD, true)
                    color(NamedTextColor.YELLOW)
                }
            )

            plugin.safetyLoginsConfig.safetyLogins.remove(player.uniqueId)
        }
    }

    private fun registerCommands(plugin: VanilandPlugin) {
        plugin.registerCommand(setSafetyLoginCommand(plugin))
    }

    private fun setSafetyLoginCommand(plugin: VanilandPlugin) = command<CommandSender>("setSafetySpawn".lowercase()) {
        val player by offlinePlayer("player")
        val reason by string("reason")

        required { it.hasPermission(Permissions.ADMIN) }

        runs {
            if (source !is Player) {
                source.sendMessage("This command can execute only in game")
                return@runs
            }
            val location = (source as Player).location

            setSafetyLogin(plugin, player, location, reason)

            source.sendMessage(
                text {
                    content("${player.name}の次回ログイン時の座標を${location.formattedString()}に変更しました")
                    color(NamedTextColor.GREEN)
                }
            )
        }
    }

    fun setSafetyLogin(plugin: VanilandPlugin, player: OfflinePlayer, location: Location, reason: String) {
        runBlocking(Dispatchers.IO) {
            plugin.safetyLoginsConfig.apply {
                safetyLogins[player.uniqueId] = SafetyLoginConfigNode(location, reason)
                save()
            }
        }
    }
}
