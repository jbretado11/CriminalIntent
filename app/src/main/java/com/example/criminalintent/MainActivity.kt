
package com.example.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrimeRepository.database = Room.databaseBuilder(
            applicationContext,
            CrimeDatabase::class.java,
            "crime-database"
        ).build()

        CrimeRepository.loadCrimes()

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CrimeListFragment()
                )
                .commit()
        }
    }
}
