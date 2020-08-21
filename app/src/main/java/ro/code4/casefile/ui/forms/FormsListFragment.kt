package ro.code4.casefile.ui.forms

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.adapters.FormDelegationAdapter
import ro.code4.casefile.databinding.FragmentFormsBinding
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.ui.base.ViewModelFragment

class FormsListFragment : ViewModelFragment<FormsViewModel>() {

    companion object {
        val TAG = FormsListFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_forms
    override val screenName: Int
        get() = R.string.analytics_title_forms

    override lateinit var viewModel: FormsViewModel
    private var beneficiaryId: Int = 0
    private lateinit var binding: FragmentFormsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = getSharedViewModel(from = { requireParentFragment() })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beneficiaryId = Parcels.unwrap(arguments?.getParcelable((Constants.PATIENT)))
        binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FormDelegationAdapter(
            viewModel::navigateToFillDate,
            beneficiaryId,
            null
        )
        viewModel.beneficiaryBarInfo().observe(viewLifecycleOwner, Observer { patientBarUiModel ->
            binding.changeBeneficiaryBar.beneficiaryBarUiModel = patientBarUiModel
        })
        viewModel.forms().observe(viewLifecycleOwner, Observer {
            Log.d("ITEMS", "${it.size}")
            adapter.items = it
        })

        viewModel.setTitle(getString(R.string.title_forms_list))

        binding.formsList.apply {
            layoutManager = LinearLayoutManager(mContext)
            this.adapter = adapter
            addItemDecoration(
                HorizontalDividerItemDecoration.Builder(activity)
                    .color(Color.TRANSPARENT)
                    .sizeResId(R.dimen.margin_16dp).build()
            )
        }
        viewModel.setPatient(beneficiaryId)
    }

    override fun onResume() {
        super.onResume()

        viewModel.getBeneficiary(beneficiaryId)
        viewModel.getForms(beneficiaryId)
    }
}
