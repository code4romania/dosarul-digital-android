package ro.code4.casefile.ui.forms.filldate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentFillDateBinding
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.helper.toSimpleDate
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.widget.spinner.ClickListener
import ro.code4.casefile.widget.spinner.SpinnerUiModel

class FillDateFragment : ViewModelFragment<FillDateViewModel>() {
    override val screenName: Int
        get() = R.string.fill_date
    override val layout: Int
        get() = R.layout.fragment_fill_date
    override val viewModel: FillDateViewModel by viewModel()
    private lateinit var parentViewModel: FormsViewModel
    private lateinit var binding: FragmentFillDateBinding

    private val datePicker = DatePickerWithListener {
        viewModel.dateSelected = it
    }

    companion object {
        val TAG = FillDateFragment::class.java.simpleName
    }

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

        parentViewModel.beneficiaryBarInfo()
            .observe(viewLifecycleOwner, Observer { patientBarUiModel ->
                binding.changeBeneficiaryBar.beneficiaryBarUiModel = patientBarUiModel
            })

        viewModel.uiSubject.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.fillDateSpinner.spinnerUiModel =
                SpinnerUiModel(it.toSimpleDate(), R.string.fill_date)
        })

        viewModel.continueObserver().observe(viewLifecycleOwner, Observer {
            parentViewModel.navigateToFormQuestions(it)
        })

        binding.fillDateSpinner.spinnerListeners =
            ClickListener(this::dateFieldListener)

        binding.continueListener = ClickListener {
            viewModel.continueSelected()
        }

        viewModel.title().observe(viewLifecycleOwner, Observer {
            parentViewModel.setTitle(it)
        })


        viewModel.setData(Parcels.unwrap(arguments?.getParcelable((Constants.FORM))))

        return binding.root
    }

    private fun dateFieldListener(view: View) {
        datePicker.showPicker(view.context, null)
    }
}
