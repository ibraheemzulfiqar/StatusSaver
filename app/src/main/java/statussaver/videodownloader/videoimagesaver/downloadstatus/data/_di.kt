package statussaver.videodownloader.videoimagesaver.downloadstatus.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.AnalyticsHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesStatusRepository(
        @ApplicationContext context: Context,
        analyticsHelper: AnalyticsHelper,
    ) = StatusRepository(context, analyticsHelper)

}