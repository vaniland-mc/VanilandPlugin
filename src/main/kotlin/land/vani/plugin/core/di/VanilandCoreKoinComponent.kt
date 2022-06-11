package land.vani.plugin.core.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface VanilandCoreKoinComponent : KoinComponent {
    override fun getKoin(): Koin = VanilandCoreKoinContext.koinApp.koin
}
