package ro.code4.casefile.adapters

import android.content.Context
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_question_details.view.*
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.QuestionDetailsListItem
import ro.code4.casefile.databinding.ItemQuestionDetailsBinding
import ro.code4.casefile.databinding.LayoutAnswerTextBinding
import ro.code4.casefile.databinding.SpinnerFieldBinding
import ro.code4.casefile.helper.Constants.TYPE_DATE
import ro.code4.casefile.helper.Constants.TYPE_MULTI_CHOICE
import ro.code4.casefile.helper.Constants.TYPE_MULTI_CHOICE_DETAILS
import ro.code4.casefile.helper.Constants.TYPE_NUMBER
import ro.code4.casefile.helper.Constants.TYPE_SINGLE_CHOICE
import ro.code4.casefile.helper.Constants.TYPE_SINGLE_CHOICE_DETAILS
import ro.code4.casefile.helper.Constants.TYPE_TEXT
import ro.code4.casefile.helper.TextWatcherDelegate
import ro.code4.casefile.helper.fromApiFormat
import ro.code4.casefile.helper.toApiFormat
import ro.code4.casefile.helper.toSimpleDate
import ro.code4.casefile.ui.forms.filldate.DatePickerWithListener
import ro.code4.casefile.ui.forms.model.QuestionDetailsUiModel
import ro.code4.casefile.widget.AnswerRadioGroup
import ro.code4.casefile.widget.CheckBoxWithDetails
import ro.code4.casefile.widget.RadioButtonWithDetails
import ro.code4.casefile.widget.spinner.ClickListener
import ro.code4.casefile.widget.spinner.SpinnerUiModel

class QuestionDetailsAdapter constructor(
    private val context: Context,
    private val formId: Int,
    private val addNoteListener: (Int, Int) -> Unit
) : ListAdapter<QuestionDetailsListItem, QuestionDetailsAdapter.QuestionDetailsViewHolder>(
    DIFF_CALLBACK
) {

    private var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        bottomMargin = context.resources.getDimensionPixelSize(R.dimen.margin_16dp)
    }

    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuestionDetailsListItem>() {
            override fun areItemsTheSame(
                oldItem: QuestionDetailsListItem,
                newItem: QuestionDetailsListItem
            ): Boolean =
                oldItem.questionDetailsUiModel.questionId == newItem.questionDetailsUiModel.questionId

            override fun areContentsTheSame(
                oldItem: QuestionDetailsListItem,
                newItem: QuestionDetailsListItem
            ): Boolean =
                oldItem.questionDetailsUiModel.equals(newItem.questionDetailsUiModel)
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long =
        getItem(position).questionDetailsUiModel.questionId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionDetailsViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_question_details, parent, false)

        when (viewType) {
            TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> {
                val radioGroup = AnswerRadioGroup(context)
                radioGroup.orientation = VERTICAL
                radioGroup.id = R.id.answersRadioGroup
                view.findViewById<LinearLayout>(R.id.answersLayout).addView(radioGroup)
            }
        }

        return QuestionDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionDetailsViewHolder, position: Int) {
        val questionDetailsUiModel = getItem(position).questionDetailsUiModel

        when (getItemViewType(position)) {
            //TODO only for testing purposes.
            TYPE_MULTI_CHOICE, TYPE_MULTI_CHOICE_DETAILS -> setupMultiChoice(holder, position)
            TYPE_SINGLE_CHOICE, TYPE_SINGLE_CHOICE_DETAILS -> setupSingleChoice(holder, position)
            TYPE_TEXT -> setupTextQuestion(holder, position)
            TYPE_NUMBER -> setupNumberQuestion(holder, position)
            TYPE_DATE -> setupDateQuestion(holder, position)
        }

        holder.bind(questionDetailsUiModel)

        holder.itemView.addNoteButton.setOnClickListener {
            addNoteListener.invoke(questionDetailsUiModel.questionId, formId)
        }
    }

    private fun setupSingleChoice(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        holder.itemView.answersLayout.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
            .removeAllViews()
        val questionDetailsUiModel = getItem(position).questionDetailsUiModel
        questionDetailsUiModel.answers.forEachIndexed { index, _ ->
            val checkedChangedListener =
                CompoundButton.OnCheckedChangeListener { p0, p1 ->
                    val questionDetails = getItem(position).questionDetailsUiModel
                    val answer = questionDetails.answers[index]
                    questionDetails.isQuestionSynced =
                        answer.isSelected == p1 && questionDetails.isQuestionSynced
                    answer.isSelected = p1
                    if (!p1) {
                        answer.value = null
                    }
                    holder.itemView.answersLayout.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                        .onCheckedChanged(p0, p1)
                }
            val textWatcher = object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    val questionDetails = getItem(position).questionDetailsUiModel
                    val answer = questionDetails.answers[index]
                    answer.value = p0.toString()
                    questionDetails.isQuestionSynced = false
                }
            }
            val questionDetails = getItem(position).questionDetailsUiModel
            val answer = questionDetails.answers[index]
            val view: View = if (answer.isFreeText) {
                RadioButtonWithDetails(context)
            } else {
                AppCompatRadioButton(
                    ContextThemeWrapper(context, R.style.RadioButton),
                    null,
                    0
                )
            }
            (view as Checkable).isChecked = answer.isSelected
            view.tag = answer.idOption

            if (answer.isFreeText) {
                view as RadioButtonWithDetails
                view.setText(answer.text)
                view.setValue(answer.value)
                view.setTextChangedListener(textWatcher)
                view.setCheckedChangedListener(checkedChangedListener)
            } else {
                view as AppCompatRadioButton
                view.text = answer.text
                view.setOnCheckedChangeListener(checkedChangedListener)
            }
            holder.itemView.findViewById<AnswerRadioGroup>(R.id.answersRadioGroup)
                .addView(view, params)
        }
    }

    private fun setupMultiChoice(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        holder.itemView.answersLayout.removeAllViews()
        val questionDetailsUiModel = getItem(position).questionDetailsUiModel
        questionDetailsUiModel.answers.forEachIndexed { index, _ ->
            val checkedChangedListener =
                CompoundButton.OnCheckedChangeListener { _, p1 ->

                    val questionDetails = getItem(position).questionDetailsUiModel
                    val answer = questionDetails.answers[index]
                    questionDetails.isQuestionSynced =
                        answer.isSelected == p1 && questionDetails.isQuestionSynced
                    answer.isSelected = p1

                    if (!p1) {
                        answer.value = null
                    }
                }
            val textWatcher = object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    val questionDetails = getItem(position).questionDetailsUiModel
                    val answer = questionDetails.answers[index]

                    answer.value = p0.toString()
                    questionDetails.isQuestionSynced = false
                }
            }
            val questionDetails = getItem(position).questionDetailsUiModel
            val answer = questionDetails.answers[index]
            val view: View = if (answer.isFreeText) {
                CheckBoxWithDetails(context)
            } else {
                AppCompatCheckBox(ContextThemeWrapper(context, R.style.CheckBox), null, 0)
            }

            (view as Checkable).isChecked = answer.isSelected
            view.tag = answer.idOption

            if (answer.isFreeText) {
                view as CheckBoxWithDetails
                view.setText(answer.text)
                view.setTextChangedListener(textWatcher)
                view.setCheckedChangedListener(checkedChangedListener)
                view.setValue(answer.value)
            } else {
                view as AppCompatCheckBox
                view.text = answer.text
                view.setOnCheckedChangeListener(checkedChangedListener)
            }

            holder.itemView.answersLayout.addView(view, params)
        }
    }

    private fun setupDateQuestion(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.answersLayout.removeAllViews()
        val questionDetailsUiModel = getItem(position).questionDetailsUiModel
        questionDetailsUiModel.answers.firstOrNull()?.let { answer ->
            //TODO firstOrNull?! it will probably be just one freeText answer
            val binding: SpinnerFieldBinding =
                SpinnerFieldBinding.inflate(LayoutInflater.from(holder.itemView.context))
            val spinnerView = binding.root.rootView

            spinnerView.isSelected = answer.isSelected
            val persistedDate = answer.value.fromApiFormat()
            binding.spinnerUiModel =
                //TODO answer.value.fromApiFormat()?.toSimpleDate() clearly this is not the best way to do it.
                // it will be formatted in the ViewModel
                SpinnerUiModel(
                    persistedDate?.toSimpleDate() ?: "",
                    spinnerHint = R.string.select_date_hint
                )
            binding.spinnerListeners = ClickListener {
                DatePickerWithListener { calendar ->
                    answer.isSelected = true
                    spinnerView.isSelected = answer.isSelected

                    val selectedDate = calendar.time.toSimpleDate()
                    answer.value = calendar.time.toApiFormat()
                    val model = SpinnerUiModel(selectedDate, R.string.select_date_hint)
                    binding.spinnerUiModel = model

                    questionDetailsUiModel.isQuestionSynced = false
                }.showPicker(spinnerView.context, persistedDate)
            }

            spinnerView.tag = answer.idOption
            holder.itemView.answersLayout.addView(spinnerView, params)
        }
    }

    private fun setupTextQuestion(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.answersLayout.removeAllViews()

        val questionDetailsUiModel = getItem(position).questionDetailsUiModel
        questionDetailsUiModel.answers.firstOrNull()?.let { answer ->
            val textWatcher = object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                    val questionDetails = getItem(position).questionDetailsUiModel
                    questionDetails.answers.firstOrNull()?.let {
                        answer.value = text.toString().trim()
                        questionDetails.isQuestionSynced = false
                        answer.isSelected = text.isNotEmpty()
                    }
                }
            }
            val binding: LayoutAnswerTextBinding =
                LayoutAnswerTextBinding.inflate(LayoutInflater.from(holder.itemView.context))
            binding.answerText = answer.value ?: ""
            binding.answerInput.addTextChangedListener(textWatcher)
            binding.answerInput.inputType = TYPE_CLASS_TEXT
            binding.root.tag = answer.idOption
            holder.itemView.answersLayout.addView(binding.root, params)
        }
    }

    private fun setupNumberQuestion(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.answersLayout.removeAllViews()

        val questionDetailsUiModel = getItem(position).questionDetailsUiModel
        questionDetailsUiModel.answers.firstOrNull()?.let { answer ->
            val textWatcher = object : TextWatcher by TextWatcherDelegate {
                override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                    val questionDetails = getItem(position).questionDetailsUiModel
                    questionDetails.answers.firstOrNull()?.let {
                        answer.value = text.toString().trim()
                        questionDetails.isQuestionSynced = false
                        answer.isSelected = text.isNotEmpty()
                    }
                }
            }
            val binding: LayoutAnswerTextBinding =
                LayoutAnswerTextBinding.inflate(LayoutInflater.from(holder.itemView.context))
            binding.answerText = answer.value ?: ""
            //TODO no design yet, nothing more for now.
            binding.answerInput.hint = binding.root.context.getString(R.string.question_number_hint)
            binding.answerInput.inputType = TYPE_CLASS_NUMBER
            binding.answerInput.addTextChangedListener(textWatcher)
            binding.root.tag = answer.idOption
            holder.itemView.answersLayout.addView(binding.root, params)
        }
    }

    override fun getItemViewType(position: Int): Int =
        getItem(position).questionDetailsUiModel.questionType

    public override fun getItem(position: Int): QuestionDetailsListItem = super.getItem(position)

    class QuestionDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemQuestionDetailsBinding.bind(view)

        fun bind(questionDetailsUiModel: QuestionDetailsUiModel) {
            binding.questionDetailsUiModel = questionDetailsUiModel
            binding.executePendingBindings()
        }
    }
}
