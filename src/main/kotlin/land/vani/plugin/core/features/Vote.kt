@file:Suppress("SameParameterValue")

package land.vani.plugin.core.features

import com.vexsoftware.votifier.model.VotifierEvent
import land.vani.mcorouhlin.paper.event.on
import land.vani.mcorouhlin.paper.item.editMeta
import land.vani.mcorouhlin.paper.item.itemStack
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.util.giveItemOrDrop
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.random.Random
import kotlin.random.nextInt
import org.bukkit.Sound as BukkitSound

object Vote : Feature {
    override val key: Feature.Key = Feature.Key("vote")

    override suspend fun onEnable(plugin: VanilandPlugin) = plugin.events {
        on<VotifierEvent> { event ->
            val player = plugin.server.getPlayer(event.vote.username)

            if (player == null || player.world.name in plugin.mainConfig.voteBlacklistedWorlds) {
                with(plugin.mainConfig) {
                    voteBonusAwaitingPlayers[event.vote.username.lowercase()] =
                        (voteBonusAwaitingPlayers[event.vote.username.lowercase()] ?: 0) + 1
                }
                return@on
            }
            giveBonusOnOnline(plugin.server, player, event.vote.serviceName)
        }
        on<PlayerJoinEvent> { event ->
            giveBonus(plugin, event.player)
        }
        on<PlayerChangedWorldEvent> { event ->
            if (event.player.world.name in plugin.mainConfig.voteBlacklistedWorlds) {
                return@on
            }
            giveBonus(plugin, event.player)
        }
    }

    private fun giveBonusOnOnline(server: Server, player: Player, serviceName: String?) {
        val isLucky = chance(percentage = 3)
        val amount = if (isLucky) {
            LUCKY_BONUS_AMOUNT
        } else {
            1
        }
        val item = makeBonusItem(amount)

        player.giveItemOrDrop(item)
        sendMessage(player, serviceName, amount)

        if (isLucky) {
            server.broadcast(
                text {
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
                }
            )
        }
        player.playSound(
            Sound.sound(
                BukkitSound.ENTITY_PLAYER_LEVELUP,
                Sound.Source.PLAYER,
                1f,
                0f
            )
        )
    }

    private fun giveBonus(plugin: VanilandPlugin, player: Player) = with(plugin.mainConfig) {
        val amount = voteBonusAwaitingPlayers[player.name.lowercase()] ?: return@with
        if (amount == 0) return
        voteBonusAwaitingPlayers[player.name.lowercase()] = 0

        val item = makeBonusItem(amount)
        player.giveItemOrDrop(item)
        sendMessage(player, null, amount)
    }

    private fun sendMessage(player: Player, serviceName: String? = null, amount: Int) {
        player.sendMessage(
            text {
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
            }
        )
    }

    private fun makeBonusItem(amount: Int): ItemStack = itemStack(
        Material.PAPER,
        amount,
    ) {
        editMeta<ItemMeta> {
            displayName(BONUS_ITEM_NAME)
            lore(BONUS_ITEM_LORE)

            setCustomModelData(BONUS_ITEM_CUSTOM_MODEL_DATA)
        }
    }

    private val BONUS_ITEM_NAME = text {
        content("バニランド アイテム引換券")
        color(NamedTextColor.AQUA)
        decoration(TextDecoration.BOLD, true)
    }

    private val BONUS_ITEM_LORE = listOf(
        text {
            content("サーバーリストで投票すると貰える券")
            color(NamedTextColor.GRAY)
        },
        text {
            content("運営が設置する施設でいろいろなものが買えるらしい…？")
            color(NamedTextColor.GRAY)
        }
    )

    private const val BONUS_ITEM_CUSTOM_MODEL_DATA = 100001

    private const val CHANCE_MIN = 1
    private const val CHANCE_MAX = 100
    private fun chance(percentage: Int): Boolean = Random.nextInt(CHANCE_MIN..CHANCE_MAX) < percentage

    private const val LUCKY_BONUS_AMOUNT = 64
}
