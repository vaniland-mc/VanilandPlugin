package land.vani.plugin.core.features

import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.command.arguments.world
import land.vani.mcorouhlin.paper.event.on
import land.vani.mcorouhlin.paper.inventory.McorouhlinInventory
import land.vani.mcorouhlin.paper.inventory.inventory
import land.vani.mcorouhlin.paper.inventory.openInventory
import land.vani.mcorouhlin.paper.item.editMeta
import land.vani.mcorouhlin.paper.item.itemStack
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.plugin.core.Permissions
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.WorldWarpNode
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.CitizensEnableEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.api.trait.TraitInfo
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL

object WorldWarpNpc : Feature<WorldWarpNpc>() {
    override val key: Key<WorldWarpNpc> = Key("worldWarpNpc")

    override suspend fun onEnable(plugin: VanilandPlugin) {
        registerTraits(plugin)
        registerCommands(plugin)
    }

    private fun registerTraits(plugin: VanilandPlugin) = plugin.events {
        on<CitizensEnableEvent> {
            CitizensAPI.getTraitFactory().registerTrait(
                TraitInfo.create(WorldWarpTrait::class.java)
            )
        }
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun registerCommands(plugin: VanilandPlugin) {
        val command = command<CommandSender>("spawnWorldWarpNpc") {
            required { it.hasPermission(Permissions.ADMIN) }

            val world by world("world")

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

                val worlds = plugin.worldWarpNpcsConfig.worlds
                    .filter { it.location.world != world }

                val npc = CitizensAPI.getNPCRegistry().createNPC(
                    EntityType.PLAYER,
                    "§b旅先案内人"
                ).apply {
                    addTrait(
                        WorldWarpTrait().apply {
                            this.worlds = worlds
                        }
                    )
                }
                npc.spawn((source as Player).location)
                // https://ja.namemc.com/skin/e49233a91392135b
                (npc.entity as Player).playerProfile.textures.skin = URL(
                    "https://s.namemc.com/i/e49233a91392135b.png"
                )
            }
        }

        plugin.registerCommand(command)
    }

    class WorldWarpTrait : Trait("worldWarpTrait") {
        private val plugin = JavaPlugin.getPlugin(VanilandPlugin::class.java)
        private val inventory: McorouhlinInventory by lazy {
            fun McorouhlinInventory.addSlot(slot: Int, node: WorldWarpNode) {
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
                        val worldWarpMobNpc = plugin.featuresRegistry
                            .getFeature(WorldWarpMobNpc.key) ?: return@onPostClick
                        worldWarpMobNpc.mobSelections.remove(event.whoClicked)
                            ?.forEach { mob ->
                                mob.teleport(node.location)
                            }
                    }
                }
            }

            @Suppress("MagicNumber")
            plugin.inventory(
                text {
                    content("移動先のワールドを選択してください")
                    color(NamedTextColor.YELLOW)
                },
                InventoryType.HOPPER
            ) {
                default(itemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
                addSlot(0, worlds[0])
                addSlot(2, worlds[1])
                addSlot(4, worlds[2])
                addSlot(6, worlds[3])
            }
        }

        lateinit var worlds: List<WorldWarpNode>

        @EventHandler
        fun onClick(event: NPCRightClickEvent) {
            if (event.npc != npc) return

            event.clicker.openInventory(inventory)
        }
    }
}
