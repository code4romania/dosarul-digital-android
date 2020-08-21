package ro.code4.casefile.ui.forms

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.helper.Constants.ADD_FAMILY_MEMBER
import ro.code4.casefile.helper.Constants.EDIT_NOTE
import ro.code4.casefile.helper.Constants.EDIT_PATIENT
import ro.code4.casefile.helper.Constants.FORM
import ro.code4.casefile.helper.Constants.PATIENT
import ro.code4.casefile.helper.Constants.QUESTION
import ro.code4.casefile.helper.Constants.QUESTION_DETAILS_PAYLOAD
import ro.code4.casefile.helper.clearBackStack
import ro.code4.casefile.helper.replaceFragment
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.filldate.FillDateFragment
import ro.code4.casefile.ui.forms.questions.QuestionsDetailsFragment
import ro.code4.casefile.ui.forms.questions.QuestionsListFragment
import ro.code4.casefile.ui.forms.selection.FormsSelectionListFragment
import ro.code4.casefile.ui.login.ProgressDialogAction
import ro.code4.casefile.ui.main.MainActivity
import ro.code4.casefile.ui.navigation.*
import ro.code4.casefile.ui.notes.NoteFragment
import ro.code4.casefile.ui.patient.PatientDetailsFragment
import ro.code4.casefile.ui.patient.addpatient.AddPatientFragment
import ro.code4.casefile.ui.section.selection.PatientsListFragment
import ro.code4.casefile.widget.ProgressDialogFragment

class FormsFragment : ViewModelFragment<FormsViewModel>() {
    override val screenName: Int
        get() = R.string.menu_beneficiaries

    override val layout: Int
        get() = R.layout.fragment_main

    override val viewModel: FormsViewModel by viewModel()

    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentFragmentManager.beginTransaction()
            .setPrimaryNavigationFragment(this)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.title().observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).setTitle(it)
        })

        viewModel.progressDialogAction().observe(viewLifecycleOwner, Observer { action ->
            when (action) {
                ProgressDialogAction.SHOW -> progressDialog.showNow(
                    childFragmentManager,
                    ProgressDialogFragment.TAG
                )
                ProgressDialogAction.DISMISS -> if (progressDialog.isResumed) progressDialog.dismiss()
            }
        })

        viewModel.navigationLiveData().observe(viewLifecycleOwner, Observer { navigationEvent ->
            when (navigationEvent) {
                is NavigationFormQuestions ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        QuestionsListFragment(),
                        bundleOf(Pair(FORM, Parcels.wrap(navigationEvent.selectedFormInfo))),
                        QuestionsListFragment.TAG
                    )
                is NavigationFormFillDate ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        FillDateFragment(),
                        bundleOf(Pair(FORM, Parcels.wrap(navigationEvent.selectedFormInfo))),
                        FillDateFragment.TAG
                    )
                is NavigationQuestionDetails ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        QuestionsDetailsFragment(),
                        bundleOf(
                            Pair(QUESTION_DETAILS_PAYLOAD, Parcels.wrap(navigationEvent.payload)),
                            Pair(QUESTION, Parcels.wrap(navigationEvent.questionId))
                        ),
                        QuestionsDetailsFragment.TAG
                    )
                is NavigationQuestionNote ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        NoteFragment(),
                        bundleOf(
                            Pair(QUESTION, Parcels.wrap(navigationEvent.questionId)),
                            Pair(PATIENT, Parcels.wrap(navigationEvent.beneficiaryId)),
                            Pair(FORM, Parcels.wrap(navigationEvent.formId))
                        ),
                        NoteFragment.TAG
                    )
                is NavigationAddPatient ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        AddPatientFragment(),
                        null,
                        AddPatientFragment.TAG
                    )
                is NavigationEditPatient ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        AddPatientFragment(),
                        bundleOf(Pair(EDIT_PATIENT, Parcels.wrap(navigationEvent.patientId))),
                        AddPatientFragment.TAG
                    )
                is NavigationAddFamilyMember ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        AddPatientFragment(),
                        bundleOf(Pair(ADD_FAMILY_MEMBER, Parcels.wrap(navigationEvent.patientId))),
                        AddPatientFragment.TAG
                    )
                is NavigationPatientDetails ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        PatientDetailsFragment(),
                        bundleOf(Pair(PATIENT, Parcels.wrap(navigationEvent.patientId))),
                        "${PatientDetailsFragment.TAG}${navigationEvent.patientId}"
                    )
                is NavigationPatientDetailsForm ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        FormsListFragment(),
                        bundleOf(Pair(PATIENT, Parcels.wrap(navigationEvent.patientId))),
                        FormsListFragment.TAG
                    )
                is NavigationPatientsList -> {
                    childFragmentManager.clearBackStack()
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        PatientsListFragment()
                    )
                }
                is NavigationPatientFormSelection ->
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        FormsSelectionListFragment(),
                        bundleOf(
                            Pair(PATIENT, Parcels.wrap(navigationEvent.patient))
                        ),
                        FormsSelectionListFragment.TAG
                    )
                is NavigationAddBeneficiaryNote -> {
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        NoteFragment(),
                        bundleOf(Pair(PATIENT, Parcels.wrap(navigationEvent.beneficiaryId))),
                        NoteFragment.TAG
                    )
                }
                is NavigationEditBeneficiaryNote -> {
                    childFragmentManager.replaceFragment(
                        R.id.content,
                        NoteFragment(),
                        bundleOf(
                            Pair(PATIENT, Parcels.wrap(navigationEvent.beneficiaryId)),
                            Pair(EDIT_NOTE, Parcels.wrap(navigationEvent.noteId))
                        ),
                        NoteFragment.TAG
                    )
                }
            }
        })

        childFragmentManager.replaceFragment(
            R.id.content,
            PatientsListFragment()
        )
    }
}
