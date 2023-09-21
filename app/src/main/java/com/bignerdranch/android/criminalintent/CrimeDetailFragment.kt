package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID



class CrimeDetailFragment : Fragment() {

    private var _binding: FragmentCrimeDetailBinding? = null

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }


            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { _, bundle ->
            val newTime =
                bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newTime) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            val timestamp = crime.date.time
            val formattedCrimeDate = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.US).format(crime.date)
            //crimeDate.text = crime.date.toString()
            crimeDate.text = formattedCrimeDate

            crimeDate.setOnClickListener {
                // Make a copy of the current crime date
//                val updatedCrimeDate = Date(crime.date.time)
//
//                // Create a Calendar instance and set it to the copied date
//                val calendar = Calendar.getInstance()
//                calendar.time = updatedCrimeDate
//
//                // Add 8 hours and 30 minutes to the copied date
//                calendar.add(Calendar.HOUR_OF_DAY, 8)
//                calendar.add(Calendar.MINUTE, 30)
//
//                // Get the updated date from the Calendar
//                updatedCrimeDate.time = calendar.timeInMillis
//
//                // Now, updatedCrimeDate contains the updated date and time
//                Log.d("test", updatedCrimeDate.toString())
//
//                // Update the UI to display the updated date
//                binding.crimeDate.text = updatedCrimeDate.toString()

                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }



            crimeTime.text = SimpleDateFormat("hh:mm a", Locale.US).format(crime.date)
            crimeTime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectTime(crime.date)
                )
            }

            crimeSolved.isChecked = crime.isSolved
        }
    }



}
