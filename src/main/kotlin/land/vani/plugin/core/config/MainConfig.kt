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
}
