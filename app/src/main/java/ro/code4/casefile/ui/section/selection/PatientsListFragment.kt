package ro.code4.casefile.ui.section.selection

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentPatientListBinding
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.ui.navigation.NavigationAddPatient
import ro.code4.casefile.ui.patient.model.PatientUiModel
import ro.code4.casefile.widget.ProgressDialogFragment

class PatientsListFragment : ViewModelFragment<PatientsViewModel>() {

    private lateinit var binding: FragmentPatientListBinding
    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }

    companion object {
        val TAG = PatientsListFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_patient_list
    override val screenName: Int
        get() = R.string.analytics_title_station_selection

    override val viewModel: PatientsViewModel by viewModel()

    private lateinit var parentViewModel: FormsViewModel
    private lateinit var adapter: PatientsAdapter
    private var patients = mutableListOf<PatientUiModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel(from = { requireParentFragment() })
        adapter = PatientsAdapter(patients,
            patientDetailsCallback = { patientId ->
                viewModel.patientDetails(patientId)
            },
            fillFormCallback = { patientId ->
                viewModel.fillPatientForm(patientId)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layout, container, false)

        binding.listeners = PatientSelectListener(this::addPatient)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.patients().observe(viewLifecycleOwner, Observer { patients ->
            Log.d("PVM", "onActivityCreated: ${patients.size}")
//            progressDialog.dismiss()
            adapter.patients.clear()

            adapter.patients.addAll(patients)
            adapter.notifyDataSetChanged()

            if (patients.isNotEmpty()) {
                binding.emptyListLayout.visibility = View.GONE
                binding.patientList.visibility = View.VISIBLE
            } else {
                binding.emptyListLayout.visibility = View.VISIBLE
                binding.patientList.visibility = View.GONE
            }
        })

        viewModel.patientDetails().observe(viewLifecycleOwner, Observer { patientId ->
            parentViewModel.navigateToPatientDetails(patientId)
        })

        viewModel.fillPatientForm().observe(viewLifecycleOwner, Observer { patientId ->
            parentViewModel.navigateToPatientForms(patientId)
        })

        viewModel.navigation().observe(viewLifecycleOwner, Observer { navigationEvent ->
            when (navigationEvent) {
                is NavigationAddPatient -> parentViewModel.navigateToAddPatient()
            }
        })
    }

    override fun onDestroyView() {
        if (progressDialog.isResumed) progressDialog.dismissAllowingStateLoss()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentViewModel.setTitle(getString(R.string.title_patients_list))

        viewModel.getPatients()

        binding.patientList.adapter = adapter
        binding.patientList.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
    }

    private fun addPatient() {
        viewModel.addPatient()
    }
}
