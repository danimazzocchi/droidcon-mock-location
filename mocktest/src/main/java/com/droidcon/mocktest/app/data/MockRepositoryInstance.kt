package com.droidcon.mocktest.app.data

import android.app.Application
import com.droidcon.mocktest.app.data.source.local.MockLocationLocalDataSource
import com.droidcon.mocktest.app.domain.MockLocationRepository

object MockRepositoryInstance {
    @Volatile
    private var INSTANCE: MockLocationRepository? = null

    fun getInstance(application: Application): MockLocationRepository =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildRepository(application).also { INSTANCE = it }
        }

    private fun buildRepository(application: Application) =
        MockLocationRepositoryImpl(MockLocationLocalDataSource(application))
}