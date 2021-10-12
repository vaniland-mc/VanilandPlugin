package land.vani.plugin.listener

import com.github.syari.spigot.api.event.Events
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.gateway.mcbans.MCBansGateway
import land.vani.plugin.permission.NOTIFY_MCBANS_LOOKUP
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

private val scope = VanilandPlugin + Job()

fun Events.mcBansLookup(
    mcBansGateway: MCBansGateway,
) {
    event<AsyncPlayerPreLoginEvent> { event ->
        scope.launch {
            delay(2000)
            val player = event.playerProfile
            val response = mcBansGateway.lookupPlayer(player.id!!)

            if (response.run { local.isEmpty() && global.isEmpty() }) {
                return@launch
            }

            val message = text {
                content("=====================================================\n")
                color(NamedTextColor.YELLOW)
            } + text {
                content("警告")
                color(NamedTextColor.RED)
                decoration(TextDecoration.BOLD, true)
            } + text {
                content(": MCBansでBAN履歴のあるプレイヤー")
                color(NamedTextColor.WHITE)
            } + text {
                content(" ${player.name} ")
                color(NamedTextColor.RED)
                decoration(TextDecoration.BOLD, false)
                hoverEvent(
                    text {
                        content("クリックで")
                    } + text {
                        content("${player.name}")
                        color(NamedTextColor.RED)
                    } + text {
                        content(" に監視モードでテレポート")
                        color(NamedTextColor.WHITE)
                    }
                )
                clickEvent(ClickEvent.runCommand("/inspector tp ${player.name}"))
            } + text {
                content("がログインしました\n")
                color(NamedTextColor.WHITE)
                decoration(TextDecoration.BOLD, false)
            } + text {
                content("情報")
                color(NamedTextColor.GREEN)
                decoration(TextDecoration.BOLD, true)
            } + text {
                content(": GlobalBan件数: ${response.global.size}件\n")
                color(NamedTextColor.WHITE)
                decoration(TextDecoration.BOLD, false)
            } + text {
                content("       LocalBan件数: ${response.local.size}件\n")
                color(NamedTextColor.WHITE)
                decoration(TextDecoration.BOLD, false)
            } + text {
                content("=====================================================\n")
                color(NamedTextColor.YELLOW)
                decoration(TextDecoration.BOLD, false)
            }

            Bukkit.getOnlinePlayers()
                .filter { it.hasPermission(NOTIFY_MCBANS_LOOKUP) }
                .map { it as CommandSender }
                .toMutableList()
                .apply { add(Bukkit.getConsoleSender()) }
                .forEach { admin ->
                    admin.sendMessage(message)
                }
        }
    }
}
