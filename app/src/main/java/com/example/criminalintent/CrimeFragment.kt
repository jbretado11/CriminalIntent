
package com.example.criminalintent

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.util.Calendar
import java.util.UUID
import android.text.Editable
import android.text.TextWatcher
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File


class CrimeFragment : Fragment(R.layout.fragment_crime) {

    private var crime = Crime()
    private var isDeleted = false

    private var photoUri: Uri? = null

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                view?.findViewById<ImageView>(R.id.crime_photo)?.let {
                    displayPhoto(it)
                }
            }
        }

    private fun displayPhoto(imageView: ImageView) {
        val file = File(requireContext().filesDir, "${crime.id}.jpg")
        if (file.exists() && file.length() > 0) {
            imageView.setImageBitmap(
                BitmapFactory.decodeFile(file.absolutePath)
            )
        }
    }

    companion object {
        private const val ARG_CRIME_ID = "crime_id"

        fun newInstance(crimeId: UUID): CrimeFragment {
            return CrimeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CRIME_ID, crimeId.toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crimeId = arguments
            ?.getString(ARG_CRIME_ID)
            ?.let { UUID.fromString(it) }

        if (crimeId != null) {
            crime = CrimeRepository.getCrime(crimeId) ?: Crime()
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val titleField =
            view.findViewById<EditText>(R.id.crime_title)

        val dateButton =
            view.findViewById<Button>(R.id.crime_date)

        val solvedCheckBox =
            view.findViewById<CheckBox>(R.id.crime_solved)





        titleField.setText(crime.title)

        titleField.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        dateButton.text =
            DateFormat.getDateInstance().format(crime.date)

        solvedCheckBox.isChecked = crime.isSolved

        solvedCheckBox.setOnCheckedChangeListener { _, checked ->
            crime.isSolved = checked
        }

        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = crime.date

            android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    crime.date = calendar.time

                    dateButton.text =
                        DateFormat.getDateInstance().format(crime.date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val cameraButton =
            view.findViewById<Button>(R.id.crime_camera)

        val photoImage =
            view.findViewById<ImageView>(R.id.crime_photo)

        displayPhoto(photoImage)

        cameraButton.setOnClickListener {
            val file = File(requireContext().filesDir, "${crime.id}.jpg")

            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

            takePicture.launch(photoUri!!)
        }

        val reportButton =
            view.findViewById<Button>(R.id.crime_report)

        reportButton.setOnClickListener {
            val solvedText = if (crime.isSolved) "Solved" else "Unsolved"

            val report = """
        Crime: ${crime.title}
        Date: ${crime.date}
        Status: $solvedText
    """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Crime Report")
                putExtra(Intent.EXTRA_TEXT, report)
            }

            startActivity(Intent.createChooser(intent, "Share Crime Report"))
        }

        val deleteButton =
            view.findViewById<Button>(R.id.crime_delete)

        deleteButton.setOnClickListener {
            isDeleted = true
            CrimeRepository.deleteCrime(crime)
            parentFragmentManager.popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()

        if (!isDeleted) {
            CrimeRepository.saveCrime(crime)
        }
    }
}
