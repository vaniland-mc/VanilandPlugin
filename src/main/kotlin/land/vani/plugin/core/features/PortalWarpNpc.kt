package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.arguments.enum
import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.command.arguments.world
import land.vani.mcorouhlin.paper.event.on
import land.vani.mcorouhlin.paper.inventory.inventory
import land.vani.mcorouhlin.paper.inventory.openInventory
import land.vani.mcorouhlin.paper.item.editMeta
import land.vani.mcorouhlin.paper.item.itemStack
import land.vani.plugin.core.VanilandPlugin
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.CitizensEnableEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.api.trait.TraitInfo
import net.citizensnpcs.trait.LookClose
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import java.util.UUID

object PortalWarpNpc : Feature<PortalWarpNpc>() {
    override val key: Key<PortalWarpNpc> = Key("portalWarpNpc")

    override suspend fun onEnable(plugin: VanilandPlugin) {
        registerTraits(plugin)
        registerCommands(plugin)
    }

    private fun registerTraits(plugin: VanilandPlugin) = plugin.events {
        on<CitizensEnableEvent> {
            CitizensAPI.getTraitFactory().registerTrait(
                TraitInfo.create(PortalWarpTrait::class.java)
            )
        }
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun registerCommands(plugin: VanilandPlugin) {
        val command = command<CommandSender>("spawnPortalWarpNpc") {
            val world by world("world")
            val locationType by enum<_, LocationType>("locationType")

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

                val config = plugin.portalWarpNpcsConfig.warpPortals[world]
                if (config == null) {
                    sendMessage(
                        source,
                        text {
                            content("No warp portals found for world $world")
                        }
                    )
                    return@runs
                }
                val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "&aポータル管理人").apply {
                    val location1 = when (locationType) {
                        LocationType.Spawn -> LocationType.PortalA to config.portalALocation
                        else -> LocationType.Spawn to config.spawnLocation
                    }

                    val location2 = when (locationType) {
                        LocationType.Spawn -> LocationType.PortalB to config.portalBLocation
                        LocationType.PortalA -> LocationType.PortalB to config.portalBLocation
                        LocationType.PortalB -> LocationType.PortalA to config.portalALocation
                    }

                    isProtected = true

                    addTrait(LookClose::class.java)
                    addTrait(
                        PortalWarpTrait().apply {
                            firstLocation = location1
                            secondLocation = location2
                        }
                    )
                }
                npc.spawn((source as Player).location)
                // https://ja.namemc.com/skin/732bfc0f4495454d
                (npc.entity as Player).playerProfile.textures.skin = URL(
                    "https://s.namemc.com/i/732bfc0f4495454d.png"
                )
            }
        }

        plugin.registerCommand(command)
    }

    private fun makePlayerHead(skinUrl: String): ItemStack = itemStack(Material.PLAYER_HEAD) {
        editMeta<SkullMeta> {
            playerProfile = Bukkit.createProfile(UUID.randomUUID()).apply {
                textures.skin = URL(skinUrl)
            }
        }
    }

    // https://minecraft-heads.com/custom-heads/decoration/45672-bird-house-oak
    private val SPAWN_HEAD = makePlayerHead(
        "https://textures.minecraft.net/texture/97f82aceb98fe069e8c166ced00242a76660bbe07091c92cdde54c6ed10dcff9"
    )

    // https://minecraft-heads.com/custom-heads/alphabet/164-oak-wood-a
    private val PORTAL_A_HEAD = makePlayerHead(
        "https://textures.minecraft.net/texture/a67d813ae7ffe5be951a4f41f2aa619a5e3894e85ea5d4986f84949c63d7672e"
    )

    //  https://minecraft-heads.com/custom-heads/alphabet/165-oak-wood-b
    private val PORTAL_B_HEAD = makePlayerHead(
        "https://textures.minecraft.net/texture/50c1b584f13987b466139285b2f3f28df6787123d0b32283d8794e3374e23"
    )

    enum class LocationType(val asPlayerHead: ItemStack) {
        Spawn(SPAWN_HEAD),
        PortalA(PORTAL_A_HEAD),
        PortalB(PORTAL_B_HEAD),
    }

    class PortalWarpTrait : Trait("portalWarpTrait") {
        private val plugin = JavaPlugin.getPlugin(VanilandPlugin::class.java)

        lateinit var firstLocation: Pair<LocationType, Location>
        lateinit var secondLocation: Pair<LocationType, Location>

        private val inventory = plugin.inventory(
            text {
                content("移動先を選択してください")
                color(NamedTextColor.YELLOW)
            },
            InventoryType.HOPPER,
        ) {
            default(itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
            slot(index = 1, firstLocation.first.asPlayerHead) {
                onPostClick { event ->
                    event.whoClicked.teleport(firstLocation.second)
                }
            }

            slot(index = 3, secondLocation.first.asPlayerHead) {
                onPostClick { event ->
                    event.whoClicked.teleport(secondLocation.second)
                }
            }
        }

        @EventHandler
        fun onClick(event: NPCRightClickEvent) {
            if (event.npc != npc) return

            event.clicker.openInventory(inventory)
        }
    }
}
