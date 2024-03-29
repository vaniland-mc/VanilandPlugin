package land.vani.plugin.core.features.commands

import com.mojang.brigadier.exceptions.CommandSyntaxException
import land.vani.mcorouhlin.paper.event.on
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.features.Feature
import org.bukkit.event.player.PlayerJoinEvent

class Commands(
    private val plugin: VanilandPlugin,
) : Feature<Commands>() {
    companion object : Key<Commands>("commands")

    override val key: Key<Commands> = Companion

    override suspend fun onEnable() {
        plugin.events {
            on<PlayerJoinEvent> { event ->
                event.player.updateCommands()
            }
        }

        CommandSyntaxException.BUILT_IN_EXCEPTIONS = BrigadierBuiltinExceptionProvider
    }
}
