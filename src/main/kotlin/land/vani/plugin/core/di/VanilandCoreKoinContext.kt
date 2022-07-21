package land.vani.plugin.core.di

import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

internal object VanilandCoreKoinContext {
    private var koin: KoinApplication? = null

    val koinApp: KoinApplication
        get() = koin ?: error("Koin is not initialized yet")

    fun start(applicationDeclaration: KoinAppDeclaration) {
        koin = koinApplication(applicationDeclaration)
    }

    fun stop() {
        koin?.close()
        koin = null
    }
}

fun startVanilandKoin(applicationDeclaration: KoinAppDeclaration) {
    VanilandCoreKoinContext.start(applicationDeclaration)
}

fun stopVanilandKoin() {
    VanilandCoreKoinContext.stop()
}
