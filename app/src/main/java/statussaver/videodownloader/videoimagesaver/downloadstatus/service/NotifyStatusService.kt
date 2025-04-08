package statussaver.videodownloader.videoimagesaver.downloadstatus.service

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.RingtoneManager
import android.os.Build
import android.os.FileObserver
import android.os.IBinder
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.FileUtils.getFileObserver
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.FileUtils.getProviderPath
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PackageManagerUtils.getInstalledStatusProviders
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PermissionUtils.hasNotificationPermission
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusRepository
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.UserPreference
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.MainActivity
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class NotifyStatusService : Service() {

    @Inject
    lateinit var preference: UserPreference

    @Inject
    lateinit var repository: StatusRepository

    private val fileObservers = mutableListOf<FileObserver>()

    private val ioDispatcher = Dispatchers.IO.limitedParallelism(1)
    private val externalScope = CoroutineScope(ioDispatcher + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        logd("onCreate")

        externalScope.launch {
            initializeFileObservers()
        }
    }

    private fun initializeFileObservers() {
        val installedProviders = getInstalledStatusProviders(this)

        val observers = installedProviders.fastMap { provider ->
            val providerDir = File(getProviderPath(provider))

            getFileObserver(
                file = providerDir,
                mask = FileObserver.ALL_EVENTS
            ) { event, path ->
                onFileObserveChange(provider, event, path)
            }
        }

        runNonFatal {
            fileObservers.addAll(observers)
            fileObservers.forEach { it.startWatching() }
        }

        /*if (preference.autoSaveEnable.value) {
            installedProviders.fastForEach(repository::saveAllStatuses)
        }*/
    }

    private fun onFileObserveChange(
        provider: StatusProvider,
        mask: Int,
        fileName: String?,
    ) {
        if (fileName.isNullOrEmpty()) return
        if (mask != FileObserver.CREATE && mask != FileObserver.MOVED_TO) return

        logd("onFileObserveChange, new status: $fileName")

        if (preference.notificationEnable.value) {
            logd("onFileObserveChange, sending notification.")
            notifyNewStatus()
        }

        if (preference.autoSaveEnable.value) {
            logd("onFileObserveChange, auto saving")

            externalScope.launch {
                repository.saveNewStatus(fileName, provider)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logd("onStartCommand, action: ${intent?.action}")

        if (ACTION_STOP_SERVICE == intent?.action) {
            preference.setNotificationEnable(false)
            preference.setAutoSaveEnable(false)

            stopSelf()

            return START_STICKY
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground()
        }

        return START_STICKY
    }

    private fun startForeground() {
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(MainActivity.EXTRA_OPEN_SETTINGS, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            /*context */ this,
            /*requestCode */ Random.nextInt(),
            /*intent */ mainActivityIntent,
            /*flags */ PendingIntent.FLAG_IMMUTABLE,
        )


        val stopServiceIntent = Intent(this, NotifyStatusService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }

        val stopPendingIntent = PendingIntent.getService(
            /*context */ this,
            /*requestCode */ 0,
            /*intent */ stopServiceIntent,
            /*flags */ PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_status_notification)
            setContentIntent(pendingIntent)
            setContentTitle(getText(R.string.Service_notify_status_title))
            setContentText(getText(R.string.Service_notify_status_description))
            addAction(0, getString(R.string.stop), stopPendingIntent)
        }.build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SERVICE_CHANNEL_ID, SERVICE_CHANNEL_ID,
                NotificationManager.IMPORTANCE_MIN
            )
            notificationManager.createNotificationChannel(channel)
        }

        runNonFatal {
            ServiceCompat.startForeground(
                /*service */ this,
                /*id */ SERVICE_NOTIFICATION_ID,
                /*notification */ notification,
                /*foregroundServiceType */ ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        }
    }

    private fun notifyNewStatus() {
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            /*context */ this,
            /*requestCode */ Random.nextInt(),
            /*intent */ mainActivityIntent,
            /*flags */ PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, STATUS_ALERT_CHANNEL_ID)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_status_notification)
            .setContentIntent(pendingIntent)
            .setContentTitle(getText(R.string.Service_new_status_title))
            .setContentText(getText(R.string.Service_new_status_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                STATUS_ALERT_CHANNEL_ID, STATUS_ALERT_CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        runNonFatal {
            notificationManager.notify(
                /*id */ MEDIA_NOTIFICATION_ID,
                /*notification */ notification
            )
        }
    }

    override fun onDestroy() {
        runNonFatal {
            fileObservers.forEach { it.stopWatching() }
            fileObservers.clear()
        }
        logd("onDestroy")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "NotifyStatusService"

        const val SERVICE_CHANNEL_ID = "Status Notification"
        const val STATUS_ALERT_CHANNEL_ID = "New Status Alert"
        const val SERVICE_NOTIFICATION_ID = 5001
        const val MEDIA_NOTIFICATION_ID = 5002

        const val ACTION_STOP_SERVICE = "stop_service"

        fun start(context: Context) {
            runNonFatal("start_service") {
                if (!hasNotificationPermission(context) || isRunning(context)) return

                val serviceIntent = Intent(context, NotifyStatusService::class.java)

                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }

        fun stop(context: Context) {
            runNonFatal("stop_service") {
                context.stopService(Intent(context, NotifyStatusService::class.java))
            }
        }

        fun isRunning(context: Context): Boolean {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?

            try {
                val services = manager?.getRunningServices(Int.MAX_VALUE).orEmpty()

                for (service in services) {
                    if (NotifyStatusService::class.java.name == service.service.className) {
                        return true
                    }
                }

                return false
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        private fun logd(message: Any?) {
            Timber.tag(TAG).d(message.toString())
        }

        private fun loge(message: Any?) {
            if (message is Exception) {
                Timber.tag(TAG).e(message)
            } else {
                Timber.tag(TAG).e(message.toString())
            }
        }
    }


}