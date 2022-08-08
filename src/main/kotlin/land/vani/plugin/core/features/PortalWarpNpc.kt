package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.inventory.inventory
import land.vani.mcorouhlin.paper.inventory.openInventory
import land.vani.mcorouhlin.paper.item.editMeta
import land.vani.mcorouhlin.paper.item.itemStack
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.PortalWarpNpcsConfig
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.net.URL
import java.util.UUID

class PortalWarpNpc(
    private val plugin: VanilandPlugin,
    private val portalWarpNpcsConfig: PortalWarpNpcsConfig,
) : Feature<PortalWarpNpc>() {
    companion object : Key<PortalWarpNpc>("portalWarpNpc") {
        private fun makePlayerHead(skinUrl: String, displayName: String): ItemStack = itemStack(Material.PLAYER_HEAD) {
            editMeta<SkullMeta> {
                playerProfile = Bukkit.createProfile(UUID.randomUUID()).apply {
                    textures.skin = URL(skinUrl)
                }
                displayName(
                    text {
                        content(displayName)
                        color(NamedTextColor.YELLOW)
                    }
                )
            }
        }

        // https://minecraft-heads.com/custom-heads/decoration/45672-bird-house-oak
        private val SPAWN_HEAD = makePlayerHead(
            "https://textures.minecraft.net/texture/" +
                "97f82aceb98fe069e8c166ced00242a76660bbe07091c92cdde54c6ed10dcff9",
            "ワールドスポーン"
        )

        // https://minecraft-heads.com/custom-heads/alphabet/164-oak-wood-a
        private val PORTAL_A_HEAD = makePlayerHead(
            "https://textures.minecraft.net/texture/" +
                "a67d813ae7ffe5be951a4f41f2aa619a5e3894e85ea5d4986f84949c63d7672e",
            "ポータルA"
        )

        //  https://minecraft-heads.com/custom-heads/alphabet/165-oak-wood-b
        private val PORTAL_B_HEAD = makePlayerHead(
            "https://textures.minecraft.net/texture/" +
                "50c1b584f13987b466139285b2f3f28df6787123d0b32283d8794e3374e23",
            "ポータルB"
        )
    }

    override val key: Key<PortalWarpNpc> = Companion

    override suspend fun onEnable() {
        registerCommands()
    }

    private suspend fun registerCommands() {
        val command = command<CommandSender>("portalWarpNpc") {
            required { it.hasPermission(Permissions.PORTAL_WARP) }

            runs {
                if (source !is Player) {
                    source.sendMessage(text { content("You must be a player to use this command.") })
                    return@runs
                }

                val inventory = plugin.inventory(
                    text {
                        content("移動先を選択してください")
                        color(NamedTextColor.YELLOW)
                    },
                    InventoryType.HOPPER,
                ) {
                    default(itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
                    val portals = portalWarpNpcsConfig.warpPortals[(source as Player).world] ?: return@inventory
                    slot(index = 0, SPAWN_HEAD) {
                        onPostClick {
                            (source as Player).teleport(portals.spawnLocation)
                        }
                    }
                    slot(index = 2, PORTAL_A_HEAD) {
                        onPostClick {
                            (source as Player).teleport(portals.portalALocation)
                        }
                    }
                    slot(index = 4, PORTAL_B_HEAD) {
                        onPostClick {
                            (source as Player).teleport(portals.portalBLocation)
                        }
                    }
                }

                (source as Player).openInventory(inventory)
            }
        }

        plugin.registerCommand(command)
    }
}
