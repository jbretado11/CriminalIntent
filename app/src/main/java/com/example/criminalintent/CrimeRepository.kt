
package com.example.criminalintent

import java.util.UUID
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
object CrimeRepository {
    lateinit var database: CrimeDatabase

    private val scope = CoroutineScope(Dispatchers.IO)

    var onCrimesChanged: (() -> Unit)? = null
    fun loadCrimes() {
        scope.launch {
            database.crimeDao().getCrimes().collect { savedCrimes ->
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    crimes.clear()
                    crimes.addAll(savedCrimes)
                    onCrimesChanged?.invoke()
                }
            }
        }
    }



    fun saveCrime(crime: Crime) {
        scope.launch {
            database.crimeDao().updateCrime(crime)
        }
    }



    val crimes = mutableListOf<Crime>()


    fun getCrime(id: UUID): Crime? {
        return crimes.find { it.id == id }
    }


    fun addCrime(crime: Crime) {
        crimes.add(crime)

        scope.launch {
            database.crimeDao().addCrime(crime)
        }
    }



    fun deleteCrime(crime: Crime) {
        crimes.removeAll { it.id == crime.id }

        scope.launch {
            database.crimeDao().deleteCrime(crime)
        }
    }

}
