package land.vani.plugin.core.di

import com.onarandombox.MultiverseCore.MultiverseCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.config.AutoMessagesConfig
import land.vani.plugin.core.config.MainConfig
import land.vani.plugin.core.config.OpInventoryConfig
import land.vani.plugin.core.config.PortalWarpNpcsConfig
import land.vani.plugin.core.config.ResetWorldConfig
import land.vani.plugin.core.config.SafetyLoginsConfig
import land.vani.plugin.core.config.WorldWarpNpcsConfig
import land.vani.plugin.core.features.AutoMessage
import land.vani.plugin.core.features.AutoRestart
import land.vani.plugin.core.features.Newbie
import land.vani.plugin.core.features.OpCommand
import land.vani.plugin.core.features.PortalWarpNpc
import land.vani.plugin.core.features.ResetWorld
import land.vani.plugin.core.features.SafetyLogin
import land.vani.plugin.core.features.VanilandCommand
import land.vani.plugin.core.features.Vote
import land.vani.plugin.core.features.WorldWarpMobNpc
import land.vani.plugin.core.features.WorldWarpNpc
import land.vani.plugin.core.features.commands.Commands
import net.citizensnpcs.Citizens
import net.citizensnpcs.api.npc.NPCRegistry
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPerms
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun modulesWithFeatures(plugin: VanilandPlugin, mainConfig: MainConfig) = buildList {
    add(coreModule(plugin, mainConfig))
    add(configModule())
    add(featuresModule())
    add(adopterModule())
}

fun coreModule(plugin: VanilandPlugin, mainConfig: MainConfig) = module {
    single { plugin }
    single { mainConfig }
    single { MiniMessage.miniMessage() }
}

fun configModule() = module {
    single {
        SafetyLoginsConfig(get()).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        AutoMessagesConfig(get(), get()).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        PortalWarpNpcsConfig(get()).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        WorldWarpNpcsConfig(get(), get()).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        OpInventoryConfig(get()).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
    single {
        ResetWorldConfig(get()).apply {
            runBlocking(Dispatchers.IO) {
                reload()
            }
        }
    }
}

fun featuresModule() = module {
    singleOf(::AutoMessage)
    singleOf(::Newbie)
    singleOf(::Vote)
    singleOf(::SafetyLogin)
    singleOf(::VanilandCommand)
    singleOf(::OpCommand)
    singleOf(::Commands)
    singleOf(::PortalWarpNpc)
    singleOf(::WorldWarpNpc)
    singleOf(::WorldWarpMobNpc)
    singleOf(::ResetWorld)
    singleOf(::AutoRestart)

    single(named("features")) {
        mapOf(
            AutoMessage to lazy { get<AutoMessage>() },
            Newbie to lazy { get<Newbie>() },
            Vote to lazy { get<Vote>() },
            SafetyLogin to lazy { get<SafetyLogin>() },
            VanilandCommand to lazy { get<VanilandCommand>() },
            OpCommand to lazy { get<OpCommand>() },
            Commands to lazy { get<Commands>() },
            PortalWarpNpc to lazy { get<PortalWarpNpc>() },
            WorldWarpNpc to lazy { get<WorldWarpNpc>() },
            WorldWarpMobNpc to lazy { get<WorldWarpMobNpc>() },
            ResetWorld to lazy { get<ResetWorld>() },
            AutoRestart to lazy { get<AutoRestart>() }
        )
    }
}

fun adopterModule() = module {
    single {
        get<VanilandPlugin>().server.servicesManager.getRegistration(LuckPerms::class.java)!!.provider
    }
    single {
        get<LuckPerms>().userManager
    }
    single {
        (get<VanilandPlugin>().server.pluginManager.getPlugin("Citizens") as Citizens)
    }
    single<NPCRegistry> {
        get<Citizens>().npcRegistry
    }
    single {
        get<VanilandPlugin>().server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore
    }
}
