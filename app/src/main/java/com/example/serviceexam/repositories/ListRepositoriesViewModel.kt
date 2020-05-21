package com.example.serviceexam.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceexam.repositories.network.Properties
import com.example.serviceexam.repositories.network.RepositoryApi
import kotlinx.coroutines.launch

class ListRepositoriesViewModel : ViewModel() {

    private val _listRepositories = MutableLiveData<List<Properties>>()
    private val _onError = MutableLiveData<Boolean>()

    val listRepositories: LiveData<List<Properties>>
        get() = _listRepositories

    val onError: LiveData<Boolean>
        get() = _onError

    fun getRepositoriesProperties() {
        viewModelScope.launch {
            val getPropertiesDeferred = RepositoryApi.retrofitService.getProperties()
            try {
                val listResult = getPropertiesDeferred.await()
                if (listResult.isNotEmpty()) {
                    _listRepositories.value = listResult
                }
            } catch (e: Exception) {
                _onError.value = true
            }

        }
    }
}