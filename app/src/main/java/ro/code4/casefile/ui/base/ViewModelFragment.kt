package ro.code4.casefile.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import ro.code4.casefile.R
import ro.code4.casefile.helper.AppExceptions
import ro.code4.casefile.interfaces.Layout
import ro.code4.casefile.interfaces.ViewModelSetter

abstract class ViewModelFragment<out T : BaseViewModel> : BaseAnalyticsFragment(), Layout,
    ViewModelSetter<T> {
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    fun showDefaultErrorSnackbar(view: View, error: AppExceptions) {
        when (error) {
            AppExceptions.INVALID_EMAIL -> showErrorSnackbar(
                view,
                getString(R.string.error_generic)
            )
            AppExceptions.GENERIC_ERROR -> showErrorSnackbar(
                view,
                getString(R.string.error_generic)
            )
            AppExceptions.NO_INTERNET -> showErrorSnackbar(
                view,
                getString(R.string.error_no_connection)
            )
        }
    }

    fun showErrorSnackbar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }
}
