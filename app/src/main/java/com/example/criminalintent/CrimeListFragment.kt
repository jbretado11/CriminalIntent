
package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class CrimeListFragment : Fragment() {

    private val crimes = CrimeRepository.crimes

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_crime_list,
            container,
            false
        )

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.crime_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CrimeAdapter(crimes)

        val addCrimeButton =
            view.findViewById<Button>(R.id.add_crime_button)

        addCrimeButton.setOnClickListener {
            val newCrime = Crime(title = "New Crime")
            CrimeRepository.addCrime(newCrime)

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CrimeFragment.newInstance(newCrime.id)
                )
                .addToBackStack(null)
                .commit()
        }


        return view
    }


    override fun onResume() {
        super.onResume()

        CrimeRepository.onCrimesChanged = {
            view?.findViewById<RecyclerView>(
                R.id.crime_recycler_view
            )?.adapter = CrimeAdapter(CrimeRepository.crimes)
        }

        view?.findViewById<RecyclerView>(
            R.id.crime_recycler_view
        )?.adapter = CrimeAdapter(CrimeRepository.crimes)
    }

    override fun onPause() {
        CrimeRepository.onCrimesChanged = null
        super.onPause()
    }


    private class CrimeHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val titleTextView: TextView =
            view.findViewById(R.id.crime_title)

        val dateTextView: TextView =
            view.findViewById(R.id.crime_date)

        val solvedImageView: ImageView =
            view.findViewById(R.id.crime_solved)
    }

    private class CrimeAdapter(private val crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CrimeHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_crime, parent, false)

            return CrimeHolder(view)
        }


        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]

            holder.titleTextView.text = crime.title
            holder.dateTextView.text = crime.date.toString()

            holder.solvedImageView.visibility =
                if (crime.isSolved) View.VISIBLE else View.GONE

            holder.itemView.setOnClickListener {
                val activity = holder.itemView.context as? MainActivity

                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(
                        R.id.fragment_container,
                        CrimeFragment.newInstance(crime.id)
                    )
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }

        override fun getItemCount(): Int = crimes.size
    }
}
