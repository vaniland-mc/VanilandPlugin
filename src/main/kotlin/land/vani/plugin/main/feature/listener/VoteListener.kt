package land.vani.plugin.main.feature.listener

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
import org.bukkit.inventory.ItemStack
import kotlin.random.Random
import kotlin.random.nextInt

fun Events.registerVoteListener(config: VoteConfig) {
    event<VotifierEvent> { event ->
        val player = Bukkit.getPlayer(event.vote.username)
        if (player == null || player.world in config.blackListWorlds) {
            config.add(event.vote.username)
            return@event
        }

        giveBonusOnOnline(player, event.vote.serviceName)
    }
    event<PlayerJoinEvent> { event ->
        config.remove(event.player.name)?.let {
            giveBonus(event.player, it)
        }
    }
    event<PlayerChangedWorldEvent> { event ->
        if (event.player.world in config.blackListWorlds) {
            return@event
        }
        config.remove(event.player.name)?.let {
            giveBonus(event.player, it)
        }
    }
}

private fun giveBonusOnOnline(player: Player, serviceName: String?) {
    val isLucky = chance(percentage = 3)
    val item = if (isLucky) {
        makeBonusItem(LUCKY_BONUS_AMOUNT)
    } else {
        makeBonusItem(1)
    }

    player.giveItemOrDrop(item)
    sendMessage(player, serviceName, item.amount)

    if (isLucky) {
        Bukkit.broadcast(text {
            content("[祝]")
            color(NamedTextColor.GOLD)
            decorate(TextDecoration.BOLD)
        } + text {
            content(player.name)
            color(NamedTextColor.AQUA)
            decoration(TextDecoration.BOLD, false)
        } + text {
            content("さんがラッキー1stチャンスに当選しました!")
            color(NamedTextColor.YELLOW)
            decoration(TextDecoration.BOLD, false)
        })
    }
}

private fun giveBonus(player: Player, amount: Int) {
    val item = makeBonusItem(amount)
    player.giveItemOrDrop(item)
    sendMessage(player, null, amount)
}

private fun sendMessage(player: Player, serviceName: String?, amount: Int) {
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
        content("を$amount 枚差し上げます！")
        color(NamedTextColor.WHITE)
    })
}

private const val BONUS_ITEM_CUSTOM_MODEL_DATA = 100001

private fun makeBonusItem(amount: Int): ItemStack = itemStack(
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

private const val PERCENTAGE_MIN = 1
private const val PERCENTAGE_MAX = 100
private fun chance(percentage: Int): Boolean = Random.nextInt(PERCENTAGE_MIN..PERCENTAGE_MAX) < percentage

private const val LUCKY_BONUS_AMOUNT = 64
