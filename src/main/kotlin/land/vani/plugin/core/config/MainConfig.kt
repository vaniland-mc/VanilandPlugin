package land.vani.plugin.core.config

import land.vani.mcorouhlin.paper.config.BukkitConfiguration
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.features.Feature

class MainConfig(plugin: VanilandPlugin) : BukkitConfiguration<MainConfig>(plugin, "config.yml") {
    val features by value<Map<String, Boolean>>("features")
        .transform(
            { raw ->
                raw.orEmpty()
                    .filterValues { it }
                    .map { (key, _) -> Feature.Key(key) }
            },
            { complex ->
                complex.associate { "$it" to true }
            }
        )

    val voteBlacklistedWorlds by value<List<String>>("voteBlacklistedWorlds")
        .transform(
            { worlds ->
                worlds?.forEach { world ->
                    if (plugin.server.getWorld(world) == null) {
                        plugin.logger.warning("World '$world' is set in config but that world is not exists")
                    }
                }

                worlds.orEmpty()
            },
            { it }
        )

    val voteBonusAwaitingPlayers by value<MutableMap<String, Int>>("voteBonusAwaitingPlayers")
        .default(mutableMapOf())
}
