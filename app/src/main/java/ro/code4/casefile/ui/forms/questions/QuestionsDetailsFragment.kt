package ro.code4.casefile.ui.forms.questions

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels
import ro.code4.casefile.R
import ro.code4.casefile.adapters.QuestionDetailsAdapter
import ro.code4.casefile.adapters.helper.QuestionDetailsListItem
import ro.code4.casefile.databinding.FragmentQuestionDetailsBinding
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.helper.Result
import ro.code4.casefile.helper.addOnLayoutChangeListenerForGalleryEffect
import ro.code4.casefile.helper.addOnScrollListenerForGalleryEffect
import ro.code4.casefile.helper.isOnline
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.ui.forms.model.SelectedFormInfo

class QuestionsDetailsFragment : ViewModelFragment<QuestionsDetailsViewModel>() {

    override val layout: Int
        get() = R.layout.fragment_question_details
    override val screenName: Int
        get() = R.string.analytics_title_question

    override val viewModel: QuestionsDetailsViewModel by viewModel()

    private lateinit var binding: FragmentQuestionDetailsBinding
    private lateinit var parentViewModel: FormsViewModel
    private lateinit var adapter: QuestionDetailsAdapter
    private var currentPosition: Int = 0
    private lateinit var layoutManager: LinearLayoutManager
    private var recyclerViewState: Parcelable? = null
    private lateinit var selectedFormInfo: SelectedFormInfo
    private var isLastAnswerSaved = false

    private var firstTime = true

    companion object {
        val TAG = QuestionsDetailsFragment::class.java.simpleName
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
        selectedFormInfo =
            Parcels.unwrap(arguments?.getParcelable((Constants.QUESTION_DETAILS_PAYLOAD)))

        adapter = QuestionDetailsAdapter(mContext, selectedFormInfo.formId, ::addNote)

        setupQuestionsList()

        binding.nextQuestionBtn.setOnClickListener {
            binding.root.requestFocus()
            if (currentPosition < adapter.itemCount - 1) {
                binding.questionsList.smoothScrollToPosition(currentPosition + 1)
            } else {
                saveLastAnswer()
                if (!mContext.isOnline()) {
                    activity?.onBackPressed()
                }
            }
        }
        binding.previousQuestionBtn.setOnClickListener {
            binding.root.requestFocus()
            if (currentPosition > 0) {
                binding.questionsList.smoothScrollToPosition(currentPosition - 1)
            }
        }
        viewModel.syncIndicator().observe(viewLifecycleOwner, Observer {
            when (it) {
                is Result.Failure -> {
                    parentViewModel.dismissProgressDialog()
                    activity?.onBackPressed()
                }
                is Result.Success -> {
                    parentViewModel.dismissProgressDialog()
                    activity?.onBackPressed()
                }
                Result.Loading -> parentViewModel.showProgressDialog()
            }
        })

        viewModel.setData(selectedFormInfo)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        parentViewModel.beneficiaryBarInfo()
            .observe(viewLifecycleOwner, Observer { patientBarUiModel ->
                binding.changeBeneficiaryBar.beneficiaryBarUiModel = patientBarUiModel
            })

        viewModel.questions().observe(viewLifecycleOwner, Observer { list ->
            setData(ArrayList(list.map { it as QuestionDetailsListItem }))
        })

        viewModel.title().observe(viewLifecycleOwner, Observer {
            parentViewModel.setTitle(it)
        })
    }

    override fun onPause() {
        if (::adapter.isInitialized && !isLastAnswerSaved) {
            val item = adapter.getItem(currentPosition)
            viewModel.saveAnswer(item, currentPosition)
        }
        super.onPause()
    }

    private fun setupQuestionsList() {
        binding.questionsList.adapter = adapter
        layoutManager = LinearLayoutManager(mContext, HORIZONTAL, false)
        binding.questionsList.layoutManager = layoutManager

        binding.questionsList.addOnScrollListenerForGalleryEffect()
        binding.questionsList.addOnLayoutChangeListenerForGalleryEffect()
        binding.questionsList.itemAnimator = null
        binding.questionsList.setHasFixedSize(true)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.questionsList)

        binding.questionsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    snapHelper.findSnapView(layoutManager)?.also {
                        val oldPos = currentPosition
                        currentPosition = layoutManager.getPosition(it)
                        val (start, end) = if (oldPos < currentPosition) {
                            Pair(oldPos, currentPosition - 1)
                        } else {
                            Pair(currentPosition + 1, oldPos)
                        }
                        for (pos in start..end) {
                            val item = adapter.getItem(pos)
                            viewModel.saveAnswer(item, pos)
                        }
                    }
                    setButtons()
                }
            }
        })
    }

    private fun setButtons() {
        val (previousBtnVisibility, nextBtnResId) = when {
            currentPosition == 0 && adapter.itemCount - 1 > 0 -> Pair(
                View.GONE,
                R.string.question_next
            )
            currentPosition == 0 && adapter.itemCount - 1 == 0 -> Pair(
                View.GONE,
                R.string.question_finish
            )
            currentPosition == adapter.itemCount - 1 -> Pair(View.VISIBLE, R.string.question_finish)
            else -> Pair(View.VISIBLE, R.string.question_next)
        }
        binding.previousQuestionBtn.visibility = previousBtnVisibility
        binding.nextQuestionBtn.text = getString(nextBtnResId)
    }

    private fun setData(items: ArrayList<QuestionDetailsListItem>) {
        adapter.submitList(items)
        if (firstTime) {
            firstTime = false

            val selectedQuestionId = Parcels.unwrap<Int>(
                arguments?.getParcelable((Constants.QUESTION))
            )
            currentPosition = items.indexOfFirst {
                it.questionDetailsUiModel.questionId == selectedQuestionId
            }
            layoutManager.scrollToPosition(currentPosition)
        } else {
            recyclerViewState = binding.questionsList.layoutManager?.onSaveInstanceState()
        }

        recyclerViewState?.let {
            binding.questionsList.layoutManager?.onRestoreInstanceState(it)
        }
        setButtons()
    }

    private fun addNote(questionId: Int, formId: Int) {
        parentViewModel.selectedNotes(questionId, formId, selectedFormInfo.beneficiaryId)
    }

    private fun saveLastAnswer() {
        val lastPos = adapter.itemCount - 1
        val item = adapter.getItem(lastPos)
        viewModel.saveAnswer(item, lastPos,
            saveCompletedListener = {
                isLastAnswerSaved = true
                if (mContext.isOnline()) {
                    viewModel.sync()
                }
            })
    }
}
