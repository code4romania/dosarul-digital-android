package ro.code4.casefile.ui.patient.addpatient

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentAddPatientBinding
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.helper.TextWatcherDelegate
import ro.code4.casefile.helper.hideKeyboard
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.ui.patient.model.AddPatientListeners
import ro.code4.casefile.ui.patient.model.AddPatientUiModel
import ro.code4.casefile.ui.patientbar.BeneficiaryBarUiModel
import ro.code4.casefile.widget.spinner.ClickListener
import ro.code4.casefile.widget.spinner.OnItemSelectedListenerAdapter
import ro.code4.casefile.widget.spinner.SameSelectionAllowedSpinner
import ro.code4.casefile.widget.spinner.SpinnerUiModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddPatientFragment : ViewModelFragment<AddPatientViewModel>() {

    companion object {
        val TAG = AddPatientFragment::class.java.simpleName
    }

    private lateinit var parentViewModel: FormsViewModel
    override val viewModel: AddPatientViewModel by viewModel()
    private lateinit var binding: FragmentAddPatientBinding
    private var countriesAdapter: ArrayAdapter<String>? = null
    private var citiesAdapter: ArrayAdapter<String>? = null
    private var addPatientModel: AddPatientModel? = null

    override val screenName: Int
        get() = R.string.analytics_title_add_patient
    override val layout: Int
        get() = R.layout.fragment_add_patient

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel(from = { requireParentFragment() })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        countriesAdapter = setupSpinner(binding.county.spinner, mutableListOf(), ::onCountySelected)
        citiesAdapter = setupSpinner(binding.city.spinner, mutableListOf(), ::onCitySelected)

        viewModel.patientUiModel().observe(viewLifecycleOwner, Observer { addPatientModel ->
            this.addPatientModel = addPatientModel
            binding.addPatientUiModel = addPatientModel.toUiModel(requireContext())
        })

        viewModel.nameUiModel().observe(viewLifecycleOwner, Observer { name ->
            binding.name = name
        })

        viewModel.patientSaved().observe(viewLifecycleOwner, Observer { patient ->
            parentViewModel.navigateToPatientFormsSelection(patient)
        })
        viewModel.countiesInSpinner().observe(viewLifecycleOwner, Observer {
            countriesAdapter?.clear()
            countriesAdapter?.addAll(it)
            citiesAdapter?.clear()
        })

        viewModel.citiesInSpinner().observe(viewLifecycleOwner, Observer {
            citiesAdapter?.clear()
            citiesAdapter?.addAll(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val editPatientId: Int? = Parcels.unwrap(arguments?.getParcelable((Constants.EDIT_PATIENT)))
        val addFamilyMemberForId: Int? =
            Parcels.unwrap(arguments?.getParcelable((Constants.ADD_FAMILY_MEMBER)))
        viewModel.initWith(editPatientId, addFamilyMemberForId)

        if(editPatientId == null) {
            parentViewModel.setTitle(getString(R.string.title_add_patient))
        } else {
            parentViewModel.setTitle(getString(R.string.title_edit_patient))
        }

        //TODO buttonVisibility=true for the new design, but let's double check if it is always displayed
        // or only when editing or adding a family member.
        binding.patientTop.beneficiaryBarUiModel =
            BeneficiaryBarUiModel(name = getString(R.string.general_data), buttonVisibility = false)

        binding.nameEdit.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.nameChanged(s.toString())
            }
        })

        binding.addPatientUiModel = getAddPatientUiModelEmptyState()
        binding.addPatientListeners = getAddPatientListeners()

        setupSpinner(
            binding.maritalStatus.spinner,
            MaritalStatus.values().map { getString(it.toUiMaritalStatus().stringRes) },
            ::onMaritalStatusSelected
        )

        setupSpinner(
            binding.gender.spinner,
            Gender.values().map { getString(it.stringRes) },
            ::onGenderSelected
        )
    }

    private fun getAddPatientListeners() = AddPatientListeners(
        dateOfBirthListener = showDatePicker(),
        maritalStatusListener = showSpinner(binding.maritalStatus.spinner),
        countyListener = showSpinner(binding.county.spinner),
        cityListener = showSpinner(binding.city.spinner),
        genderListener = showSpinner(binding.gender.spinner),
        savePatientListener = SavePatientListener { viewModel.savePatient() }
    )

    private fun showDatePicker() = ClickListener {
        activity?.hideKeyboard()
        val calendar = Calendar.getInstance()
        addPatientModel?.dateOfBirth?.let { date ->
            calendar.time = date
        }
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                viewModel.dateOfBirthSelected(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showSpinner(spinner: SameSelectionAllowedSpinner) = ClickListener {
        activity?.hideKeyboard()
        spinner.userOpenedSpinner()
        spinner.requestFocus()
        spinner.performClick()
    }

    private fun onMaritalStatusSelected(position: Int) {
        viewModel.maritalStatusSelected(MaritalStatus.values()[position])
    }

    private fun onCountySelected(position: Int) {
        viewModel.countySelected(position)
    }

    private fun onCitySelected(position: Int) {
        viewModel.citySelected(position)
    }

    private fun onGenderSelected(position: Int) {
        viewModel.genderSelected(Gender.values()[position])
    }

    private fun setupSpinner(
        spinner: AppCompatSpinner,
        values: List<String>,
        function: (Int) -> Unit
    ): ArrayAdapter<String> {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            values
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0, false)
        spinner.onItemSelectedListener = object : OnItemSelectedListenerAdapter() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                function.invoke(position)
            }
        }
        return adapter
    }

    private fun getAddPatientUiModelEmptyState() = AddPatientUiModel(
        dateOfBirthUiModel = SpinnerUiModel(spinnerHint = R.string.date_of_birth_hint),
        maritalStatusUiModel = SpinnerUiModel(spinnerHint = R.string.marital_status_hint),
        countyUiModel = SpinnerUiModel(spinnerHint = R.string.county_hint),
        cityUiModel = SpinnerUiModel(spinnerHint = R.string.city_hint),
        genderUiModel = SpinnerUiModel(spinnerHint = R.string.gender_hint)
//        familyRelationUiModel = SpinnerUiModel(spinnerHint = R.string.family_relation_hint)
    )
}

private fun AddPatientModel?.toUiModel(context: Context): AddPatientUiModel {
    val simpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_SIMPLE, Locale.US)
    return AddPatientUiModel(
        dateOfBirthUiModel = SpinnerUiModel(spinnerText = this?.dateOfBirth?.let {
            simpleDateFormat.format(it)
        } ?: "",
            spinnerHint = R.string.date_of_birth_hint),
        maritalStatusUiModel = SpinnerUiModel(
            spinnerText = this?.maritalStatus?.stringRes?.let { context.getString(it) } ?: "",
            spinnerHint = R.string.marital_status_hint
        ),
        countyUiModel = SpinnerUiModel(
            spinnerText = this?.county?.toString() ?: "",
            spinnerHint = R.string.county_hint
        ),
        cityUiModel = SpinnerUiModel(
            spinnerText = this?.city?.toString() ?: "",
            spinnerHint = R.string.city_hint
        ),
        genderUiModel = SpinnerUiModel(
            spinnerText = this?.gender?.stringRes?.let { context.getString(it) } ?: "",
            spinnerHint = R.string.gender_hint
        )
//        familyRelationUiModel = SpinnerUiModel(
//            spinnerText = this?.familyRelation?.stringRes?.let { context.getString(it) } ?: "",
//            spinnerHint = R.string.family_relation_hint
//        )
    )
}
