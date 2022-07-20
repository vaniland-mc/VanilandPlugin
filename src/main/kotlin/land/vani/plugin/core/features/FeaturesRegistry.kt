package land.vani.plugin.core.features

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import land.vani.plugin.core.VanilandPlugin

class FeaturesRegistry(
    private val plugin: VanilandPlugin,
    @PublishedApi
    internal val allFeatures: List<Feature<*>>,
) {
    suspend fun enableFeatures(toEnableFeatures: List<Feature.Key<*>>) {
        toEnableFeatures.mapNotNull { key -> this.allFeatures.find { it.key == key } }
            .map {
                plugin.launch {
                    plugin.logger.info("Enabling feature '${it.key}'")
                    it.onEnable(plugin)
                    it.isEnabled = true
                    plugin.logger.info("Enabled feature '${it.key}'")
                }
            }.joinAll()

        plugin.logger.info("Enabled all features!")
    }

    suspend fun disableFeatures(toDisableFeatures: List<Feature.Key<*>>) {
        toDisableFeatures.mapNotNull { key -> this.allFeatures.find { it.key == key } }
            .map {
                plugin.launch {
                    plugin.logger.info("Disabling feature '${it.key}'")
                    it.onDisable(plugin)
                    it.isEnabled = false
                    plugin.logger.info("Disabled feature '${it.key}'")
                }
            }.joinAll()

        plugin.logger.info("Disabled all features!")
    }

    inline fun <reified T : Feature<T>> getFeature(key: Feature.Key<T>): T? = allFeatures.find {
        it.key == key && it.isEnabled
    } as T?
}
