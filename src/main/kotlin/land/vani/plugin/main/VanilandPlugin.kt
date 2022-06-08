package land.vani.plugin.main

import com.github.syari.spigot.api.EasySpigotAPIOption
import com.github.syari.spigot.api.event.events
import dev.kord.core.Kord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import land.vani.plugin.main.di.makeModules
import land.vani.plugin.main.feature.command.inspectorCommand
import land.vani.plugin.main.feature.command.mcBansCommand
import land.vani.plugin.main.feature.command.opCommand
import land.vani.plugin.main.feature.command.vanilandCommand
import land.vani.plugin.main.feature.command.worldMenuCommand
import land.vani.plugin.main.feature.listener.registerAdvancementListener
import land.vani.plugin.main.feature.listener.registerBanManagerIntegration
import land.vani.plugin.main.feature.listener.registerGroupIntegration
import land.vani.plugin.main.feature.listener.registerNewbieListener
import land.vani.plugin.main.feature.listener.registerVoteListener
import land.vani.plugin.main.feature.timer.registerAutoMessage
import land.vani.plugin.main.feature.timer.registerAutoRestart
import land.vani.plugin.main.feature.timer.registerResetWorldSafetySpawn
import land.vani.plugin.main.feature.timer.resetWorld
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import kotlin.coroutines.CoroutineContext

class VanilandPlugin : JavaPlugin(), KoinComponent {
    override fun onEnable() {
        setupKoin()
        setupCustomInventory()
        registerFeatures()
        startKord()
    }

    override fun onDisable() {
        stopKord()
        resetWorld()
    }

    private fun setupKoin() {
        val modules = makeModules(this)
        startKoin {
            modules(modules)
        }
    }

    private fun setupCustomInventory() {
        EasySpigotAPIOption.useCustomInventory(this)
    }

    private fun registerFeatures() {
        events {
            launch { registerBanManagerIntegration(get(), get()) }
            registerGroupIntegration(get())
            registerVoteListener(get())
            registerNewbieListener()
            registerAdvancementListener()
            registerResetWorldSafetySpawn(get())
        }

        inspectorCommand()
        worldMenuCommand()
        vanilandCommand()
        mcBansCommand(get())
        opCommand()

        registerAutoMessage()
        registerAutoRestart()
    }

    private fun startKord() {
        val kord = get<Kord>()
        launch {
            kord.login()
        }
    }

    private fun stopKord() {
        val kord = get<Kord>()
        runBlocking {
            kord.logout()
        }
    }

    companion object : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job()
    }
}
