
package ro.code4.casefile.ui.forms.filldate

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.*


private var dialog: DatePickerDialog? = null

class DatePickerWithListener(private val datePickerListener: (cal: Calendar) -> Unit) {
    fun showPicker(context: Context, date: Date?) {
        val currentDialog = getDialog(context, date)
        if(currentDialog.isShowing) return
        val calendar = Calendar.getInstance()
        currentDialog.datePicker.maxDate = calendar.timeInMillis
        currentDialog.show()
    }

    private fun getDialog(context: Context, date: Date?): DatePickerDialog {
        val unwrappedDialog = dialog
        if(unwrappedDialog == null) {
            val calendar = Calendar.getInstance()
            date?.let {
                calendar.time = date
            }
            return DatePickerDialog(
                context, { _: DatePicker, i: Int, i1: Int, i2: Int ->
                    val result = Calendar.getInstance()
                    result.set(Calendar.YEAR, i)
                    result.set(Calendar.MONTH, i1)
                    result.set(Calendar.DAY_OF_MONTH, i2)
                    datePickerListener(result)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            return unwrappedDialog
        }
    }

}
