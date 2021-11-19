package land.vani.plugin.main.feature.listener

import com.destroystokyo.paper.profile.PlayerProfile
import com.github.syari.spigot.api.event.Events
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.config.DiscordConfig
import land.vani.plugin.main.config.MCBansConfig
import land.vani.plugin.main.gateway.mcbans.MCBansGateway
import land.vani.plugin.main.gateway.mcbans.model.MCBansLookupResponse
import land.vani.plugin.main.permission.NOTIFY_MCBANS_LOOKUP
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.slf4j.Logger

private val scope = VanilandPlugin + Job()

suspend fun Events.registerMCBansIntegration(
    mcBansGateway: MCBansGateway,
    mcBansConfig: MCBansConfig,
    discordConfig: DiscordConfig,
    guild: Guild,
    logger: Logger,
) {
    val channel = guild.getChannelOf<TextChannel>(Snowflake(discordConfig.mcBansNotifyChannel))

    event<AsyncPlayerPreLoginEvent> { event ->
        scope.launch {
            val player = event.playerProfile
            val response = mcBansGateway.lookupPlayer(player.id!!)
            if (response == null) {
                logger.warn("[MCBans] MCBans API is downed, we passed check player ${player.name}")
                return@launch
            }

            if (response.run { local.isEmpty() && global.isEmpty() }) {
                return@launch
            }

            if (player.id in mcBansConfig.ignoredUuidList) {
                logger.info("[MCBans] ${player.name} has ban history at MCBans, but ignored.")
                return@launch
            }

            val message = buildMessage(player, response)

            Bukkit.getOnlinePlayers()
                .filter { it.hasPermission(NOTIFY_MCBANS_LOOKUP) }
                .map { it as CommandSender }
                .toMutableList()
                .apply { add(Bukkit.getConsoleSender()) }
                .forEach { admin ->
                    admin.sendMessage(message)
                }

            sendLoginAlertToDiscord(channel, player, response)
        }
    }
}

private fun buildMessage(player: PlayerProfile, response: MCBansLookupResponse): Component = text {
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

private suspend fun sendLoginAlertToDiscord(
    channel: TextChannel,
    player: PlayerProfile,
    response: MCBansLookupResponse,
) {
    scope.launch {
        channel.createEmbed {
            author {
                name = "MCBans"
            }
            description = "Ban履歴のあるプレイヤーのログインを検知しました"
            field("MinecraftID") { "${player.name}" }
            field("UUID") { "${player.id}" }
            field("評価値") { "${response.reputation}" }
            field("GlobalBan件数") { "${response.global.size}" }
            field("LocalBan件数") { "${response.local.size}" }
        }
    }
}
