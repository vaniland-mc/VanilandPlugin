package land.vani.plugin.main.listener

import com.github.syari.spigot.api.event.Events
import com.github.syari.spigot.api.item.customModelData
import com.github.syari.spigot.api.item.itemStack
import com.vexsoftware.votifier.model.VotifierEvent
import land.vani.plugin.main.config.VoteConfig
import land.vani.plugin.main.util.giveItemOrDrop
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

fun Events.registerVoteListener(config: VoteConfig) {
    event<VotifierEvent> { event ->
        val player = Bukkit.getPlayer(event.vote.username)
        if (player == null || player.world in config.blackListWorlds) {
            config.add(event.vote.username)
            return@event
        }

        giveBonus(player, 1, event.vote.serviceName)
    }
    event<PlayerJoinEvent> { event ->
        config.remove(event.player.name)?.let {
            giveBonus(event.player, it, null)
        }
    }
    event<PlayerChangedWorldEvent> { event ->
        if (event.player.world in config.blackListWorlds) {
            return@event
        }
        config.remove(event.player.name)?.let {
            giveBonus(event.player, it, null)
        }
    }
}

private const val BONUS_ITEM_CUSTOM_MODEL_DATA = 100001

private fun giveBonus(player: Player, amount: Int, serviceName: String?) {
    val item = itemStack(
        Material.PAPER,
        "&b&lバニランド アイテム引換券",
        listOf(
            "&7サーバーリストで投票すると貰える券",
            "&7運営が設置する施設でいろいろなものが買えるらしい…？"
        ),
    ) {
        this.amount = amount
        customModelData = BONUS_ITEM_CUSTOM_MODEL_DATA
    }

    player.giveItemOrDrop(item)
    player.sendMessage(text {
        content(serviceName ?: "サーバーリスト")
        color(NamedTextColor.YELLOW)
        decorate(TextDecoration.BOLD)
    } + text {
        content("での投票ありがとうございます！\n")
        color(NamedTextColor.WHITE)
    } + text {
        content("投票のお礼として")
    } + text {
        content("アイテム引換券")
        color(NamedTextColor.AQUA)
        decorate(TextDecoration.BOLD)
    } + text {
        content("を差し上げます！")
        color(NamedTextColor.WHITE)
    })
}
