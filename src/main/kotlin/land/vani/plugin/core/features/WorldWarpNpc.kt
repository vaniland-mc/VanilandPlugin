package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.inventory.McorouhlinInventory
import land.vani.mcorouhlin.paper.inventory.inventory
import land.vani.mcorouhlin.paper.inventory.openInventory
import land.vani.mcorouhlin.paper.item.editMeta
import land.vani.mcorouhlin.paper.item.itemStack
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.WorldWarpNode
import land.vani.plugin.core.config.WorldWarpNpcsConfig
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta

class WorldWarpNpc(
    private val plugin: VanilandPlugin,
    private val worldWarpNpcsConfig: WorldWarpNpcsConfig,
) : Feature<WorldWarpNpc>() {
    companion object : Key<WorldWarpNpc>("worldWarpNpc")

    override val key: Key<WorldWarpNpc> = Companion

    override suspend fun onEnable() {
        registerCommands()
    }

    @Suppress("RemoveExplicitTypeArguments")
    private suspend fun registerCommands() {
        val command = command<CommandSender>("worldWarpNpc") {
            required { it.hasPermission(Permissions.PORTAL_WARP) }

            runs {
                if (source !is Player) {
                    sendMessage(
                        source,
                        text {
                            content("You must be a player to use this command.")
                            color(NamedTextColor.RED)
                        }
                    )
                    return@runs
                }

                val worlds = worldWarpNpcsConfig.worlds
                    .filter { it.location.world != (source as Player).world }
                val inventory = plugin.inventory(
                    text {
                        content("移動先のワールドを選択してください")
                        color(NamedTextColor.YELLOW)
                    }
                ) {
                    default(itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
                    worlds.forEachIndexed { index, node ->
                        addSlot(index * 2, node)
                    }
                }
                (source as Player).openInventory(inventory)
            }
        }

        plugin.registerCommand(command)
    }

    private fun McorouhlinInventory.addSlot(slot: Int, node: WorldWarpNode) {
        slot(
            index = slot,
            itemStack(node.material) {
                editMeta<ItemMeta> {
                    displayName(node.displayName)
                    lore(node.lore)
                }
            }
        ) {
            onPostClick { event ->
                event.whoClicked.teleport(node.location)
                MOB_SELECTIONS.remove(event.whoClicked)
                    ?.forEach { mob ->
                        mob.teleport(node.location)
                    }
            }
        }
    }
}
