package statussaver.videodownloader.videoimagesaver.downloadstatus.analytics

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AnalyticsModule {

    @Provides
    @Singleton
    fun providesAnalyticsHelper(): AnalyticsHelper {
        return DebugAnalyticsHelper()
    }

}