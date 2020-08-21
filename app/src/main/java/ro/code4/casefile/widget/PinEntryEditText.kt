package ro.code4.casefile.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import ro.code4.casefile.R

class PinEntryEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, val defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var clickListener: OnClickListener? = null

    private var iconSize = resources.getDimensionPixelSize(R.dimen.margin_24dp)
    private var lineSize = resources.getDimensionPixelSize(R.dimen.pin_entry_line_size)
    private var spaceSize = resources.getDimensionPixelSize(R.dimen.pin_entry_space_size)
    private var noOfCharacters = 4f
    private var mLineSpacing =
        resources.getDimensionPixelSize(R.dimen.pin_entry_line_spacing) //8dp by default
    private val textWidths = FloatArray(noOfCharacters.toInt())

    init {
        //Disable copy paste
        disableCopyPaste()

        //When tapped, move cursor to end of the text
        disableCursorSelection()
    }

    override fun onDraw(canvas: Canvas?) {
        // Drawable start
        val d = ContextCompat.getDrawable(context, R.drawable.ic_password)
        d?.setBounds(paddingStart, paddingTop, paddingStart + iconSize, paddingTop + iconSize)
        canvas?.let { d?.draw(it) }

        var startX = compoundPaddingStart
        val bottom = height - paddingBottom

        // Text Width
        val text = text
        val textLength = text?.length ?: 0
        paint.getTextWidths(getText(), 0, textLength, textWidths)

        for (i in 0 until noOfCharacters.toInt()) {
            canvas?.drawLine(
                startX.toFloat(),
                bottom.toFloat(),
                (startX + lineSize).toFloat(),
                bottom.toFloat(),
                paint
            )

            if (text?.length ?: i > i) {
                val middle: Float = startX + lineSize / 2f
                canvas?.drawText(
                    text.toString(),
                    i,
                    i + 1,
                    middle - textWidths[0] / 2f,
                    bottom - mLineSpacing.toFloat(),
                    paint
                )
            }

            startX += lineSize + spaceSize
        }
    }

    override fun setOnClickListener(clickListener: OnClickListener?) {
        this.clickListener = clickListener
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    private fun disableCursorSelection() {
        super.setOnClickListener { v ->
            text?.length?.let { setSelection(it) }
            clickListener?.onClick(v)
        }
        // super.setFilters()
    }

    private fun disableCopyPaste() {
        super.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {}
        })
    }
}
