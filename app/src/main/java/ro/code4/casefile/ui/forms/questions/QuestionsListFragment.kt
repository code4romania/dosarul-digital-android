package ro.code4.casefile.ui.forms.questions

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.adapters.QuestionDelegationAdapter
import ro.code4.casefile.analytics.Event
import ro.code4.casefile.analytics.Param
import ro.code4.casefile.analytics.ParamKey
import ro.code4.casefile.databinding.FragmentQuestionsBinding
import ro.code4.casefile.helper.Constants.FORM
import ro.code4.casefile.helper.Result
import ro.code4.casefile.helper.isOnline
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.ui.forms.model.SelectedFormInfo

class QuestionsListFragment : ViewModelFragment<QuestionsViewModel>() {

    override val layout: Int
        get() = R.layout.fragment_questions
    override val screenName: Int
        get() = R.string.analytics_title_questions

    private lateinit var parentViewModel: FormsViewModel
    private lateinit var selectedFormInfo: SelectedFormInfo
    private lateinit var binding: FragmentQuestionsBinding
    override val viewModel: QuestionsViewModel by viewModel()
    private val questionAdapter by lazy {
        QuestionDelegationAdapter(parentViewModel::navigateToQuestionDetails)
    }

    companion object {
        val TAG = QuestionsListFragment::class.java.simpleName
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedFormInfo = Parcels.unwrap(arguments?.getParcelable((FORM)))

        with(binding.questionsList) {
            layoutManager = LinearLayoutManager(mContext)
            adapter = questionAdapter
            addItemDecoration(
                HorizontalDividerItemDecoration.Builder(activity)
                    .color(Color.TRANSPARENT)
                    .sizeResId(R.dimen.margin_16dp).build()
            )
        }

        binding.syncButton.setOnClickListener {
            logAnalyticsEvent(Event.MANUAL_SYNC, Param(ParamKey.NUMBER_NOT_SYNCED, 0))

            if (!mContext.isOnline()) {
                Snackbar.make(
                    binding.syncButton,
                    getString(R.string.form_sync_no_internet),
                    Snackbar.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            viewModel.sync()
        }


        viewModel.setData(selectedFormInfo)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.syncIndicator().observe(viewLifecycleOwner, Observer {
            when(it){
                is Result.Failure -> {
                    parentViewModel.dismissProgressDialog()
                    viewModel.getQuestions(selectedFormInfo)
                }
                is Result.Success ->  {
                    parentViewModel.dismissProgressDialog()
                    viewModel.getQuestions(selectedFormInfo)
                }
                Result.Loading ->  parentViewModel.showProgressDialog()
            }
        })
        parentViewModel.beneficiaryBarInfo()
            .observe(viewLifecycleOwner, Observer { patientBarUiModel ->
                binding.changeBeneficiaryBar.beneficiaryBarUiModel = patientBarUiModel
            })

        viewModel.questions().observe(viewLifecycleOwner, Observer {
            questionAdapter.items = it
            questionAdapter.notifyDataSetChanged()
        })

        viewModel.title().observe(viewLifecycleOwner, Observer {
            parentViewModel.setTitle(it)
        })

        viewModel.syncVisibility().observe(viewLifecycleOwner, Observer {
            binding.syncGroup.visibility = it
        })
    }
}
