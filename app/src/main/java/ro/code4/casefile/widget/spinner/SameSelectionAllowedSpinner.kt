package ro.code4.casefile.widget.spinner

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner

/**
 * By default, a [Spinner] widget only triggers its [AdapterView.OnItemSelectedListener] only if the selected
 * element is different than the current selection. Receiving the selection of the same entry is needed so
 * we can cancel the language selection dialog, event when the user selects the same language.
 */
class SameSelectionAllowedSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatSpinner(context, attrs) {

    private var spinnerTouched = false
    private var lastSelectedPosition = -1

    override fun setSelection(position: Int) {
        super.setSelection(position)
        triggerListenerIfPossible(position)
    }

    override fun setSelection(position: Int, animate: Boolean) {
        super.setSelection(position, animate)
        triggerListenerIfPossible(position)
    }

    private fun triggerListenerIfPossible(newSelection: Int) {
        if (lastSelectedPosition == newSelection) {
            onItemSelectedListener?.onItemSelected(this, null, newSelection, -1)
        }
        lastSelectedPosition = newSelection
    }

    fun userOpenedSpinner() {
        spinnerTouched = true
    }


    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        super.setOnItemSelectedListener(object: OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                listener?.onNothingSelected(p0)
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(spinnerTouched) {
                    listener?.onItemSelected(p0, p1, p2, p3)
                }
                spinnerTouched = false
            }

        })

    }
}
