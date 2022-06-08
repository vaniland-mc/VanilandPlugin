package land.vani.plugin.main.di

import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.def.DefaultConfigResource
import com.onarandombox.MultiverseCore.MultiverseCore
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import kotlinx.coroutines.runBlocking
import land.vani.plugin.main.VanilandPlugin
import land.vani.plugin.main.config.DiscordConfig
import land.vani.plugin.main.config.MCBansConfig
import land.vani.plugin.main.config.OpInventoryConfig
import land.vani.plugin.main.config.ResetWorldConfig
import land.vani.plugin.main.config.VoteConfig
import land.vani.plugin.main.config.WorldMenuConfig
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.koin.core.module.Module
import org.koin.dsl.module

private val configsModule = module {
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "mcBans.yml", DefaultConfigResource(plugin, "mcBans.yml"))

        MCBansConfig(config)
    }
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "worldMenu.yml")

        WorldMenuConfig(config)
    }
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "discord.yml")

        DiscordConfig(config)
    }
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "vote.yml", DefaultConfigResource(plugin, "vote.yml"))

        VoteConfig(config)
    }
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "opInventory.yml")

        OpInventoryConfig(config)
    }
    single {
        val plugin = get<VanilandPlugin>()
        val config = plugin.config(Bukkit.getConsoleSender(), "resetWorld.yml")

        ResetWorldConfig(config)
    }
}

private val gatewaysModules = module {
    single {
        val config = get<DiscordConfig>()
        runBlocking {
            Kord(config.token)
        }
    }
    single {
        val config = get<DiscordConfig>()
        val kord = get<Kord>()
        runBlocking {
            kord.getGuild(Snowflake(config.guildId))
        }
    }
}

private val dependPluginsModule = module {
    single {
        Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)!!.provider
    }
    single {
        Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core") as MultiverseCore
    }
}

fun makeModules(plugin: VanilandPlugin): List<Module> {
    val pluginModule = module {
        single { plugin }
        single { plugin.slF4JLogger }
    }

    return pluginModule +
        configsModule +
        gatewaysModules +
        dependPluginsModule
}
