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
import land.vani.plugin.core.config.SafetyLoginsConfig
import land.vani.plugin.core.util.formattedString
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.PluginEnableEvent

class SafetyLogin(
    private val plugin: VanilandPlugin,
    private val safetyLoginsConfig: SafetyLoginsConfig,
) : Feature<SafetyLogin>() {
    companion object : Key<SafetyLogin>("safetyLogin")

    override val key: Key<SafetyLogin> = Companion

    override suspend fun onEnable() {
        registerEventListener()
        registerCommands()
    }

    private fun registerEventListener() = plugin.events {
        on<PluginEnableEvent> topOn@{ enableEvent ->
            if (enableEvent.plugin != plugin) return@topOn
            on<PlayerJoinEvent> { event ->
                val player = event.player
                val safetyLogin = safetyLoginsConfig.safetyLogins[player.uniqueId] ?: return@on
                event.player.teleport(safetyLogin.location)

                sendMessage(
                    player,
                    text {
                        content("運営によりログイン時にテレポートされました\n理由: ${safetyLogin.reason}")
                        decoration(TextDecoration.BOLD, true)
                        color(NamedTextColor.YELLOW)
                    }
                )

                safetyLoginsConfig.safetyLogins.remove(player.uniqueId)
            }
        }
    }

    private fun registerCommands() {
        plugin.registerCommand(setSafetyLoginCommand())
    }

    private fun setSafetyLoginCommand() = command<CommandSender>("setSafetySpawn".lowercase()) {
        val player by offlinePlayer("player")
        val reason by string("reason")

        required { it.hasPermission(Permissions.ADMIN) }

        runs {
            if (source !is Player) {
                source.sendMessage("This command can execute only in game")
                return@runs
            }
            val location = (source as Player).location

            setSafetyLogin(player, location, reason)

            source.sendMessage(
                text {
                    content("${player.name}の次回ログイン時の座標を${location.formattedString()}に変更しました")
                    color(NamedTextColor.GREEN)
                }
            )
        }
    }

    fun setSafetyLogin(player: OfflinePlayer, location: Location, reason: String) {
        runBlocking(Dispatchers.IO) {
            safetyLoginsConfig.apply {
                safetyLogins[player.uniqueId] = SafetyLoginConfigNode(location, reason)
                save()
            }
        }
    }
}
