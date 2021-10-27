package land.vani.plugin

import com.github.syari.spigot.api.EasySpigotAPIOption
import com.github.syari.spigot.api.event.events
import dev.kord.core.Kord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import land.vani.plugin.command.inspectorCommand
import land.vani.plugin.command.mcBansCommand
import land.vani.plugin.command.vanilandCommand
import land.vani.plugin.command.worldMenuCommand
import land.vani.plugin.di.makeModules
import land.vani.plugin.listener.banManager
import land.vani.plugin.listener.group.groupIntegration
import land.vani.plugin.listener.mcBansLookup
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
            runBlocking {
                mcBansLookup(get(), get(), get(), get(), get())
                groupIntegration(get())
                banManager(get(), get())
            }
        }

        inspectorCommand()
        worldMenuCommand()
        vanilandCommand()
        mcBansCommand(get())
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
