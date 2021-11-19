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
import land.vani.plugin.main.feature.listener.registerBanManagerIntegration
import land.vani.plugin.main.feature.listener.registerDisableWitherBlockBreak
import land.vani.plugin.main.feature.listener.registerExplosionListener
import land.vani.plugin.main.feature.listener.registerGroupIntegration
import land.vani.plugin.main.feature.listener.registerMCBansIntegration
import land.vani.plugin.main.feature.listener.registerNewbieListener
import land.vani.plugin.main.feature.listener.registerVoteListener
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
            launch { registerMCBansIntegration(get(), get(), get(), get(), get()) }
            launch { registerBanManagerIntegration(get(), get()) }
            registerGroupIntegration(get())
            registerVoteListener(get())
            registerExplosionListener()
            registerDisableWitherBlockBreak()
            registerNewbieListener()
        }

        inspectorCommand()
        worldMenuCommand()
        vanilandCommand()
        mcBansCommand(get())
        opCommand()
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
