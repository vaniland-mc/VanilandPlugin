package land.vani.plugin.listener

import com.github.syari.spigot.api.event.Events
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import land.vani.plugin.VanilandPlugin
import land.vani.plugin.config.DiscordConfig
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

suspend fun Events.banManager(
    guild: Guild,
    config: DiscordConfig,
) {
    val channel = guild.getChannelOf<TextChannel>(Snowflake(config.banManagerNotifyChannel))

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
                description = "${actor}がIP Banしました"
                field("IPアドレス") { ip }
                field("理由") { reason }
                field("期限") { expires }
                field("処罰日時") { created }
            }
        }
    }

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
                description = "${actor}がIP Muteしました"
                field("IPアドレス") { ip }
                field("理由") { reason }
                field("期限") { expires }
                field("処罰日時") { created }
            }
        }
    }

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
                description = "${actor}がIP範囲Banしました"
                field("IPアドレス範囲") { "$fromIp - $toIp" }
                field("理由") { reason }
                field("期限") { expires }
                field("処罰日時") { created }
            }
        }
    }

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
                description = "${actor}がプレイヤーをBanしました"
                field("MinecraftID") { playerName }
                field("UUID") { playerUuid }
                field("理由") { reason }
                field("期限") { expires }
                field("処罰日時") { created }
            }
        }
    }

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
                description = "${actor}がプレイヤーをMuteしました"
                field("MinecraftID") { playerName }
                field("UUID") { playerUuid }
                field("理由") { reason }
                field("期限") { expires }
                field("処罰日時") { created }
            }
        }
    }

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
                description = "${actor}がプレイヤーを警告しました"
                field("MinecraftID") { playerName }
                field("UUID") { playerUuid }
                field("理由") { reason }
                field("警告ポイント") { "$points" }
                field("期限") { expires }
                field("処罰日時") { created }
            }
        }
    }
}

private fun Long.toDateTimeString() =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.of("Asia/Tokyo"))
        .toLocalDateTime()
        .format(dateTimeFormatter)
