package land.vani.plugin.core.features

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import land.vani.plugin.core.VanilandPlugin
import land.vani.plugin.core.di.VanilandCoreKoinComponent

class FeaturesRegistry(
    private val plugin: VanilandPlugin,
    @PublishedApi
    internal val allFeatures: Map<Feature.Key<*>, Lazy<Feature<*>>>,
) : VanilandCoreKoinComponent {
    suspend fun enableFeatures(toEnableFeatures: List<Feature.Key<*>>) {
        println(toEnableFeatures)
        toEnableFeatures.mapNotNull { key ->
            allFeatures[key]
        }.map {
            val feature by it
            println("feature: $feature")
            plugin.launch {
                plugin.logger.info("Enabling feature '${feature.key}'")
                feature.onEnable()
                feature.isEnabled = true
                plugin.logger.info("Enabled feature '${feature.key}'")
            }
        }.joinAll()

        plugin.logger.info("Enabled all features!")
    }

    suspend fun disableFeatures(toDisableFeatures: List<Feature.Key<*>>) {
        toDisableFeatures.mapNotNull { key -> this.allFeatures[key] }
            .map {
                val feature by it
                plugin.launch {
                    plugin.logger.info("Disabling feature '${feature.key}'")
                    feature.onDisable()
                    feature.isEnabled = false
                    plugin.logger.info("Disabled feature '${feature.key}'")
                }
            }.joinAll()

        plugin.logger.info("Disabled all features!")
    }

    inline fun <reified T : Feature<T>> getFeature(key: Feature.Key<T>): T? = allFeatures[key]
        ?.takeIf {
            val feature by it
            feature.isEnabled
        } as T?
}
