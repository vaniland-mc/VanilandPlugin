package land.vani.plugin.core

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import land.vani.mcorouhlin.command.Command
import land.vani.mcorouhlin.command.register
import land.vani.mcorouhlin.paper.McorouhlinKotlinPlugin
import land.vani.mcorouhlin.permission.registerPermissions
import land.vani.plugin.core.config.AutoMessagesConfig
import land.vani.plugin.core.config.MainConfig
import land.vani.plugin.core.config.SafetyLoginsConfig
import land.vani.plugin.core.di.VanilandCoreKoinComponent
import land.vani.plugin.core.di.modulesWithFeatures
import land.vani.plugin.core.di.startVanilandKoin
import land.vani.plugin.core.di.stopVanilandKoin
import land.vani.plugin.core.features.AutoMessage
import land.vani.plugin.core.features.FeaturesRegistry
import land.vani.plugin.core.features.Newbie
import land.vani.plugin.core.features.SafetyLogin
import land.vani.plugin.core.features.VanilandCommand
import land.vani.plugin.core.features.Vote
import land.vani.plugin.core.features.commands.Commands
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin
import org.koin.core.component.inject

class VanilandPlugin : McorouhlinKotlinPlugin(), VanilandCoreKoinComponent {
    val featuresRegistry = FeaturesRegistry(
        this,
        listOf(
            AutoMessage,
            Newbie,
            Vote,
            SafetyLogin,
            VanilandCommand,
            Commands,
        )
    )

    private val commandDispatcher: CommandDispatcher<CommandSender> = CommandDispatcher()
    private val commandMutex = Mutex()

    @Suppress("MagicNumber")
    private val commandCache: Cache<Pair<String, CommandSender>, ParseResults<CommandSender>> =
        CacheBuilder.newBuilder()
            .maximumSize(10)
            .build()

    val mainConfig by inject<MainConfig>()
    val safetyLoginsConfig by inject<SafetyLoginsConfig>()
    val autoMessageConfig by inject<AutoMessagesConfig>()

    private suspend fun saveDefaultConfigs() = withContext(Dispatchers.IO) {
        saveResource("config.yml", false)
        saveResource("safetyLogins.yml", false)
        saveResource("autoMessages.yml", false)
    }

    override suspend fun onEnableAsync() {
        registerPermissions<Permissions>()

        saveDefaultConfigs()

        val mainConfig = MainConfig(this).apply {
            reload()
        }

        startVanilandKoin {
            modules(modulesWithFeatures(this@VanilandPlugin, mainConfig))
        }

        featuresRegistry.enableFeatures(mainConfig.features)
    }

    override suspend fun onDisableAsync() {
        featuresRegistry.disableFeatures(mainConfig.features)

        cancel()
        stopVanilandKoin()
    }

    fun registerCommand(command: Command<CommandSender>) {
        val bukkitCommand = object :
            org.bukkit.command.Command(command.literal),
            PluginIdentifiableCommand {
            override fun getPlugin(): Plugin = this@VanilandPlugin

            override fun execute(
                sender: CommandSender,
                commandLabel: String,
                args: Array<out String>,
            ): Boolean {
                val joinedCommand = joinCommand(command.literal, args)
                val parsedCommand = commandCache.getIfPresent(joinedCommand to sender)
                    ?: commandDispatcher.parse(joinedCommand, sender).also {
                        commandCache.put(joinedCommand to sender, it)
                    }

                try {
                    commandDispatcher.execute(parsedCommand)
                } catch (ex: CommandSyntaxException) {
                    sender.sendMessage(
                        text {
                            content(ex.message ?: "コマンドの文法にエラーがあります")
                            color(NamedTextColor.RED)
                        }
                    )
                }

                return true
            }

            override fun tabComplete(
                sender: CommandSender,
                alias: String,
                args: Array<out String>,
            ): List<String> {
                val joinedCommand = joinCommand(command.literal, args)
                val parsedCommand = commandCache.getIfPresent(joinedCommand to sender)
                    ?: commandDispatcher.parse(joinedCommand, sender).also {
                        commandCache.put(joinedCommand to sender, it)
                    }

                try {
                    return commandDispatcher.getCompletionSuggestions(parsedCommand)
                        .get()
                        .list
                        .map { it.text }
                } catch (ex: CommandSyntaxException) {
                    sender.sendMessage(
                        text {
                            content(ex.message ?: "コマンドの文法にエラーがあります")
                            color(NamedTextColor.RED)
                        }
                    )
                }

                return emptyList()
            }
        }

        launch {
            commandMutex.withLock {
                server.commandMap.register(name, bukkitCommand)
                commandDispatcher.register(command)
                slF4JLogger.info("Command ${command.literal} registered successfully")
            }
        }
    }

    private fun joinCommand(commandLabel: String, args: Array<out String>) =
        commandLabel.lowercase() + if (args.isNotEmpty()) {
            args.joinToString(separator = " ", prefix = " ")
        } else {
            ""
        }
}
