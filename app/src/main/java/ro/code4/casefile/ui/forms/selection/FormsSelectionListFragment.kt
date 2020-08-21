package ro.code4.casefile.ui.forms.selection

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentFormsSelectionBinding
import ro.code4.casefile.helper.AppExceptions
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.helper.isOnline
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.ui.patient.addpatient.AddUpdatePatientModel

class FormsSelectionListFragment : ViewModelFragment<FormsSelectionViewModel>() {

    companion object {
        val TAG = FormsSelectionListFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentFormsSelectionBinding
    private lateinit var parentViewModel: FormsViewModel
    private lateinit var patient: AddUpdatePatientModel

    override val layout: Int
        get() = R.layout.fragment_forms_selection
    override val screenName: Int
        get() = R.string.analytics_title_forms_selection
    override val viewModel: FormsSelectionViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentViewModel = getSharedViewModel(from = { requireParentFragment() })
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

        patient = Parcels.unwrap(arguments?.getParcelable((Constants.PATIENT)))

        setFragmentTitle()

        val adapter = FormsSelectionAdapter(mutableListOf(), viewModel::selectForm)

        viewModel.forms().observe(viewLifecycleOwner, Observer { result ->
            adapter.formsSelectionItems.clear()
            result.handle(
                onLoading = {
                    parentViewModel.showProgressDialog()
                },
                onSuccess = { forms ->
                    parentViewModel.dismissProgressDialog()
                    forms?.let { adapter.formsSelectionItems.addAll(it) }
                    adapter.notifyDataSetChanged()
                },
                onFailure = {
                    parentViewModel.dismissProgressDialog()
                    showDefaultErrorSnackbar(binding.continueButton, AppExceptions.GENERIC_ERROR)
                }
            )
        })


        binding.selectFormsList.apply {
            layoutManager = LinearLayoutManager(mContext)
            this.adapter = adapter
            addItemDecoration(
                HorizontalDividerItemDecoration.Builder(activity)
                    .color(Color.TRANSPARENT)
                    .sizeResId(R.dimen.margin_16dp).build()
            )
        }

        binding.continueButton.setOnClickListener {
            if (context?.isOnline() == false) {
                showDefaultErrorSnackbar(binding.continueButton, AppExceptions.NO_INTERNET)
                return@setOnClickListener
            }
            viewModel.savePatient()
        }

        viewModel.savedSelection().observe(viewLifecycleOwner, Observer { result ->
            result.handle(
                onLoading = {
                    parentViewModel.showProgressDialog()
                },
                onSuccess = {
                    parentViewModel.dismissProgressDialog()
                    parentViewModel.navigateToPatientsList()
                },
                onFailure = {
                    parentViewModel.dismissProgressDialog()
                    showDefaultErrorSnackbar(binding.continueButton, AppExceptions.GENERIC_ERROR)
                }
            )
        })

        viewModel.setPatient(patient)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.continueButtonState().observe(viewLifecycleOwner, Observer { isEnabled ->
            binding.continueButton.isEnabled = isEnabled
        })
    }

    private fun setFragmentTitle() {
        val isEditAction: Boolean = patient.beneficiaryId != null
        val title = if (isEditAction) {
            getString(R.string.title_forms_selection_edit)
        } else {
            getString(R.string.title_forms_selection)
        }
        parentViewModel.setTitle(title)
    }
}
