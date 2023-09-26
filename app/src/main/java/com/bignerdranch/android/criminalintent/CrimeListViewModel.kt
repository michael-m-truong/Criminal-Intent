package com.bignerdranch.android.criminalintent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.criminalintent.database.CrimeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "CrimeListViewModel"

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()

    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())
    val crimes: StateFlow<List<Crime>>
        get() = _crimes.asStateFlow()

    // New StateFlow to represent whether the list is empty or not
    private val _isListEmpty: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isListEmpty: StateFlow<Boolean>
        get() = _isListEmpty.asStateFlow()

    init {
        viewModelScope.launch {
            crimeRepository.getCrimes().collect {
                _crimes.value = it
                // Update the isListEmpty StateFlow based on the new list of crimes
                _isListEmpty.value = it.isEmpty()
            }
        }
    }

    suspend fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }
}
