package land.vani.plugin.main.feature.listener

import com.github.syari.spigot.api.event.Events
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.config.DiscordConfig
import me.confuser.banmanager.bukkit.api.events.IpBannedEvent
import me.confuser.banmanager.bukkit.api.events.IpMutedEvent
import me.confuser.banmanager.bukkit.api.events.IpRangeBannedEvent
import me.confuser.banmanager.bukkit.api.events.PlayerBannedEvent
import me.confuser.banmanager.bukkit.api.events.PlayerMutedEvent
import me.confuser.banmanager.bukkit.api.events.PlayerWarnedEvent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val scope = CoroutineScope(VanilandPlugin.coroutineContext + Job())

private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

suspend fun Events.registerBanManagerIntegration(
    guild: Guild,
    config: DiscordConfig,
) {
    val channel = guild.getChannelOf<TextChannel>(Snowflake(config.banManagerNotifyChannel))

    registerIpBannedListener(channel)
    registerIpMutedListener(channel)
    registerIpRangeBannedEvent(channel)
    registerPlayerBannedEvent(channel)
    registerPlayerMutedEvent(channel)
    registerPlayerWarnedEvent(channel)
}

private fun Events.registerIpBannedListener(channel: TextChannel) {
    event<IpBannedEvent> { event ->
        scope.launch {
            val actor = event.ban.actor.name
            val ip = event.ban.ip.toString()
            val reason = event.ban.reason
            val expires = event.ban.expires.toDateTimeString()
            val created = event.ban.created.toDateTimeString()

            channel.startPublicThread("IP Banned: $ip").createEmbed {
                author {
                    name = actor
                }
                description = "${actor}???IP Ban????????????"
                field("IP????????????") { ip }
                field("??????") { reason }
                field("??????") { expires }
                field("????????????") { created }
            }
        }
    }
}

private fun Events.registerIpMutedListener(channel: TextChannel) {
    event<IpMutedEvent> { event ->
        scope.launch {
            val actor = event.mute.actor.name
            val ip = event.mute.ip.toString()
            val reason = event.mute.reason
            val expires = event.mute.expires.toDateTimeString()
            val created = event.mute.created.toDateTimeString()

            channel.startPublicThread("IP Muted: $ip").createEmbed {
                author {
                    name = actor
                }
                description = "${actor}???IP Mute????????????"
                field("IP????????????") { ip }
                field("??????") { reason }
                field("??????") { expires }
                field("????????????") { created }
            }
        }
    }
}

private fun Events.registerIpRangeBannedEvent(channel: TextChannel) {
    event<IpRangeBannedEvent> { event ->
        scope.launch {
            val actor = event.ban.actor.name
            val fromIp = event.ban.fromIp.toString()
            val toIp = event.ban.toIp.toString()
            val reason = event.ban.reason
            val expires = event.ban.expires.toDateTimeString()
            val created = event.ban.created.toDateTimeString()

            channel.startPublicThread("IP Range Banned: $fromIp - $toIp").createEmbed {
                author {
                    name = actor
                }
                description = "${actor}???IP??????Ban????????????"
                field("IP??????????????????") { "$fromIp - $toIp" }
                field("??????") { reason }
                field("??????") { expires }
                field("????????????") { created }
            }
        }
    }
}

private fun Events.registerPlayerBannedEvent(channel: TextChannel) {
    event<PlayerBannedEvent> { event ->
        scope.launch {
            val actor = event.ban.actor.name
            val playerName = event.ban.player.name
            val playerUuid = event.ban.player.uuid.toString()
            val reason = event.ban.reason
            val expires = event.ban.expires.toDateTimeString()
            val created = event.ban.created.toDateTimeString()

            channel.startPublicThread("Player Banned: $playerName").createEmbed {
                author {
                    name = actor
                }
                description = "${actor}?????????????????????Ban????????????"
                field("MinecraftID") { playerName }
                field("UUID") { playerUuid }
                field("??????") { reason }
                field("??????") { expires }
                field("????????????") { created }
            }
        }
    }
}

private fun Events.registerPlayerMutedEvent(channel: TextChannel) {
    event<PlayerMutedEvent> { event ->
        scope.launch {
            val actor = event.mute.actor.name
            val playerName = event.mute.player.name
            val playerUuid = event.mute.player.uuid.toString()
            val reason = event.mute.reason
            val expires = event.mute.expires.toDateTimeString()
            val created = event.mute.created.toDateTimeString()

            channel.startPublicThread("Player Muted: $playerName").createEmbed {
                author {
                    name = actor
                }
                description = "${actor}?????????????????????Mute????????????"
                field("MinecraftID") { playerName }
                field("UUID") { playerUuid }
                field("??????") { reason }
                field("??????") { expires }
                field("????????????") { created }
            }
        }
    }
}

private fun Events.registerPlayerWarnedEvent(channel: TextChannel) {
    event<PlayerWarnedEvent> { event ->
        scope.launch {
            val actor = event.warning.actor.name
            val playerName = event.warning.player.name
            val playerUuid = event.warning.player.uuid.toString()
            val reason = event.warning.reason
            val points = event.warning.points
            val expires = event.warning.expires.toDateTimeString()
            val created = event.warning.created.toDateTimeString()

            channel.startPublicThread("Player Warned: $playerName").createEmbed {
                author {
                    name = actor
                }
                description = "${actor}???????????????????????????????????????"
                field("MinecraftID") { playerName }
                field("UUID") { playerUuid }
                field("??????") { reason }
                field("??????????????????") { "$points" }
                field("??????") { expires }
                field("????????????") { created }
            }
        }
    }
}

private fun Long.toDateTimeString() =
    if (this == 0L) {
        "?????????"
    } else {
        Instant.ofEpochSecond(this)
            .atZone(ZoneId.systemDefault())
            .format(dateTimeFormatter)
    }
