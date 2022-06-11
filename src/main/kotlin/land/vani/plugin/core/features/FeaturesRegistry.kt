package land.vani.plugin.core.features

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import land.vani.plugin.core.VanilandPlugin

class FeaturesRegistry(
    private val plugin: VanilandPlugin,
    private val features: List<Feature>,
) {
    suspend fun enableFeatures(features: List<Feature.Key>) {
        features.mapNotNull { key -> this.features.find { it.key == key } }
            .map {
                plugin.launch {
                    plugin.logger.info("Enabling feature '${it.key}'")
                    it.onEnable(plugin)
                    plugin.logger.info("Enabled feature '${it.key}'")
                }
            }.joinAll()
    }

    suspend fun disableFeatures(features: List<Feature.Key>) {
        features.mapNotNull { key -> this.features.find { it.key == key } }
            .map {
                plugin.launch {
                    plugin.logger.info("Disabling feature '${it.key}'")
                    it.onDisable(plugin)
                    plugin.logger.info("Disabled feature '${it.key}'")
                }
            }.joinAll()
    }
}
