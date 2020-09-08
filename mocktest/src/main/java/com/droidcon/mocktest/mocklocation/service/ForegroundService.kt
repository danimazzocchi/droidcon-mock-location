package com.droidcon.mocktest.mocklocation.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.droidcon.mocktest.app.MockSupportActivity
import com.droidcon.mocktest.app.data.MockRepositoryInstance
import com.droidcon.mocktest.app.domain.MockLocation
import com.droidcon.mocktest.mocklocation.MockLocationService
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandler
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandlerImpl
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandlerStatus
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class ForegroundService : LifecycleService(), MockLocationService {

    private val mBinder: IBinder = ServiceBinder()
    private lateinit var mockLocationHandler: MockLocationHandler
    private val mockLocationLiveData: MutableLiveData<Location> = MutableLiveData()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        mockLocationHandler = MockLocationHandlerImpl(this)
        MockRepositoryInstance.getInstance(application).getLocations()
            .asLiveData(ioScope.coroutineContext).observe(this, Observer {
                setMockLocationMapsPoints(it)
            })
    }

    override fun activateMockLocation() {
        mockLocationHandler.activate(object :
            MockLocationListener {
            override fun onLocationChanged(location: Location) {
                mockLocationLiveData.postValue(location)
            }

            override fun onLocationError(e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ForegroundService, "Service Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun playPauseMockLocation() {
        mockLocationHandler.playPause()
    }

    override fun stopMockLocation() {
        mockLocationHandler.stop()
    }

    override fun stopService() {
        stopSelf()
    }

    fun setMockLocationMapsPoints(locations: List<MockLocation>) {
        mockLocationHandler.setMapPoints(locations)
    }

    override fun mockLocationLiveData(): LiveData<Location> {
        return mockLocationLiveData
    }

    override fun mockLocationHandlerStatusLiveData(): LiveData<MockLocationHandlerStatus> {
        return mockLocationHandler.statusLiveData()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let { setupService(intent) }
        return START_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return mBinder
    }

    private fun setupService(intent: Intent) {
        val input = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MockSupportActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService<NotificationManager>(
                NotificationManager::class.java
            )
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    inner class ServiceBinder : Binder() {
        val service: MockLocationService
            get() = this@ForegroundService
    }

    companion object {

        const val TAG = "ForegroundService"

        /**
         * The notification channel id
         */
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }
}