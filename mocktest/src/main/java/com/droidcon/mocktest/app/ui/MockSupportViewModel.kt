package com.droidcon.mocktest.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.droidcon.mocktest.app.data.MockRepositoryInstance
import com.droidcon.mocktest.app.domain.MockLocation
import com.droidcon.mocktest.app.domain.MockLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The ViewModel to handle mock locations.
 */
class MockSupportViewModel(application: Application) : AndroidViewModel(application) {

    private var mockLocationRepository: MockLocationRepository = MockRepositoryInstance.getInstance(application)
    var markersLiveData: LiveData<List<MockLocation>> = mockLocationRepository.getLocations().asLiveData(viewModelScope.coroutineContext)

    fun askForNewMapPoint(location: MockLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            mockLocationRepository.addLocation(location)
        }
    }

    fun removeLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            mockLocationRepository.removeAllLocations()
        }
    }
}