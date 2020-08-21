package ro.code4.casefile.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ro.code4.casefile.R
import ro.code4.casefile.helper.TextWatcherDelegate
import ro.code4.casefile.helper.isValidEmail

class EditTextDialogFragment : DialogFragment(), TextView.OnEditorActionListener {

    /**
     * Listener to send input to activity.
     */
    interface EditTextDialogListener {
        /**
         * Used to send the email to opening fragment.
         */
        fun onFinishEditDialog(email: String)
    }

    var editTextDialogListener: EditTextDialogListener? = null
    private var textInputLayout: TextInputLayout? = null
    private var editText: TextInputEditText? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EditTextDialogListener) {
            editTextDialogListener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.fragment_dialog_edit_text, null)

        textInputLayout = dialogView.findViewById(R.id.emailLayout)
        editText = dialogView.findViewById(R.id.emailEditText)

        editText?.setOnEditorActionListener(this)
        editText?.requestFocus()
        editText?.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout?.error = null
            }
        })

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.send_report_dialog_title)
            .setMessage(R.string.send_report_dialog_message)
            .setView(dialogView)
            .setPositiveButton(R.string.send_report_dialog_send, null)
            .setNegativeButton(R.string.send_report_dialog_cancel) { _, _ ->
                dismiss()
            }
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                setText()
            }
        }
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return dialog
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            setText()
            return true
        }
        return false
    }

    private fun setText() {
        val text = editText?.text?.toString()?.trim()
        when {
            text.isNullOrBlank() || !text.isValidEmail() -> {
                textInputLayout?.error = getString(R.string.send_report_dialog_error)
            }
            else -> {
                editTextDialogListener?.onFinishEditDialog(text)
                dismiss()
            }
        }
    }
}
