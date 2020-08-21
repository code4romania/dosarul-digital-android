package ro.code4.casefile.ui.patient

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentPatientDetailsBinding
import ro.code4.casefile.helper.AppExceptions
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.ui.patient.addpatient.UiMaritalStatus
import ro.code4.casefile.ui.patient.model.FormHistoryUiModel
import ro.code4.casefile.ui.patient.model.PatientUiModel

class PatientDetailsFragment : ViewModelFragment<PatientDetailsViewModel>() {

    companion object {
        val TAG = PatientDetailsFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentPatientDetailsBinding
    private var selectedPatientId: Int? = null
    private lateinit var parentViewModel: FormsViewModel
    private lateinit var patientFormHistoryAdapter: PatientFormHistoryAdapter
    private lateinit var patientFamilyMembersAdapter: PatientFamilyMembersAdapter
    private lateinit var patientNotesAdapter: PatientNotesAdapter

    override val screenName: Int
        get() = R.string.analytics_title_patient_details
    override val layout: Int
        get() = R.layout.fragment_patient_details
    override val viewModel: PatientDetailsViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel(from = { requireParentFragment() })
        patientFormHistoryAdapter = PatientFormHistoryAdapter(mutableListOf(), ::selectedForm)
        patientFamilyMembersAdapter =
            PatientFamilyMembersAdapter(mutableListOf(), ::selectedFamilyMember)
        patientNotesAdapter = PatientNotesAdapter(mutableListOf(), ::selectedNotes)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.patientFormHistoryUiModel()
            .observe(viewLifecycleOwner, Observer { patientFormHistoryUiModel ->
                patientFormHistoryAdapter.formHistoryItems.clear()
                patientFormHistoryAdapter.formHistoryItems.addAll(patientFormHistoryUiModel.formHistoryList)
                patientFormHistoryAdapter.notifyDataSetChanged()

                binding.patientFormHistoryListVisibility =
                    patientFormHistoryUiModel.formHistoryListVisibility
                binding.noPatientFormHistoryLayoutVisibility =
                    patientFormHistoryUiModel.noFormHistoryLayoutVisibility
            })

        viewModel.patientFamilyMembersUiModel()
            .observe(viewLifecycleOwner, Observer { familyMembersUiModel ->
                patientFamilyMembersAdapter.familyMembers.clear()
                patientFamilyMembersAdapter.familyMembers.addAll(familyMembersUiModel.familyMembers)
                patientFamilyMembersAdapter.notifyDataSetChanged()

                binding.patientFamilyMembersListVisibility =
                    familyMembersUiModel.familyMembersListVisibility
                binding.noPatientFamilyMembersLayoutVisibility =
                    familyMembersUiModel.noFamilyMembersLayoutVisibility
            })

        viewModel.patientNotesUiModel().observe(viewLifecycleOwner, Observer { notesUiModel ->
            patientNotesAdapter.notes.clear()
            patientNotesAdapter.notes.addAll(notesUiModel.notes)
            patientNotesAdapter.notifyDataSetChanged()

            binding.patientNotesListVisibility = notesUiModel.notesListVisibility
            binding.noPatientNotesLayoutVisibility = notesUiModel.noNotesLayoutVisibility
        })

        viewModel.patientUiModel().observe(viewLifecycleOwner, Observer { patientCardUiModel ->
            binding.patientUiModel = patientCardUiModel
        })

        //todo
        // viewModel.sendRecordButtonState().observe(viewLifecycleOwner, Observer { isEnabled ->
        //     binding.sendRecordButton.isEnabled = isEnabled
        // })
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
        parentViewModel.setTitle(getString(R.string.title_patients_details))
        selectedPatientId = Parcels.unwrap(arguments?.getParcelable((Constants.PATIENT)))
        selectedPatientId?.let { patientId ->
            viewModel.getPatient(patientId)
            viewModel.getPatientFormHistory(patientId)
            viewModel.getPatientFamilyMembers(patientId)
            viewModel.getPatientNotes(patientId)
        }


        viewModel.sendRecordButtonState().observe(viewLifecycleOwner, Observer {
            it.handle(
                onSuccess = { _ ->
                    parentViewModel.dismissProgressDialog()
                },
                onLoading = {
                    parentViewModel.showProgressDialog()
                },
                onFailure = { error ->
                    // TODO: Handle errors to show personalized messages for each one
                    try {
                        parentViewModel.dismissProgressDialog()
                    } catch (e: IllegalStateException) {
                        // ok, nevermind
                    }

                    showDefaultErrorSnackbar(binding.root, AppExceptions.GENERIC_ERROR)
                }

            )
        })
        binding.patientUiModel = getEmptyPatientUiModel()
        binding.patientDetailsListeners = getPatientDetailsListeners()

        binding.formHistoryList.adapter = patientFormHistoryAdapter
        binding.formHistoryList.layoutManager = getLayoutManager(view)

        binding.familyMembersList.adapter = patientFamilyMembersAdapter
        binding.familyMembersList.layoutManager = getLayoutManager(view)

        binding.notesList.adapter = patientNotesAdapter
        binding.notesList.layoutManager = getLayoutManager(view)
    }

    private fun getLayoutManager(view: View) =
        LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

    //TODO better solution for this?
    // Databinding + stringRes -> android.content.res.Resources$NotFoundException: String resource ID #0x0
    private fun getEmptyPatientUiModel(): PatientUiModel {
        return PatientUiModel(0, "", 0, UiMaritalStatus.NOT_MARRIED, "", "")
    }

    private fun getPatientDetailsListeners() = PatientDetailsListeners(
        ::editPatient,
        ::goToForms,
        ::sendRecord,
        ::addFamilyMember,
        ::addNote
    )

    private fun editPatient() {
        selectedPatientId?.let { parentViewModel.navigateToEditPatient(it) }
    }

    private fun goToForms() {
        selectedPatientId?.let { parentViewModel.navigateToPatientForms(it) }
    }

    private fun sendRecord() {
        selectedPatientId?.let {
            viewModel.sendRecord(it)
        }

    }

    private fun addFamilyMember() {
        selectedPatientId?.let { parentViewModel.navigateToAddFamilyMember(it) }
    }

    private fun addNote() {
        selectedPatientId?.let { parentViewModel.navigateToAddBeneficiaryNote(it) }
    }

    private fun selectedForm(formUiModel: FormHistoryUiModel) {
        selectedPatientId?.let {
            val selectedFormInfo = viewModel.getSelectedFormInfo(it, formUiModel)
            parentViewModel.navigateToFormQuestions(selectedFormInfo)
        }
    }

    private fun selectedFamilyMember(familyMemberId: Int) {
        parentViewModel.navigateToPatientDetails(familyMemberId)
    }

    private fun selectedNotes(noteId: Int) {
        selectedPatientId?.let {
            parentViewModel.navigateToEditBeneficiaryNote(it, noteId)
        }
    }
}
