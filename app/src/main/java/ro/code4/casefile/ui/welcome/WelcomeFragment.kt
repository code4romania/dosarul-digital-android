
package ro.code4.casefile.ui.welcome

import android.content.Context
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.koin.android.viewmodel.ext.android.getSharedViewModel
import ro.code4.casefile.R
import ro.code4.casefile.ui.base.ViewModelFragment

class WelcomeFragment : ViewModelFragment<WelcomeViewModel>() {

    companion object {
        val TAG = WelcomeFragment::class.java.simpleName
    }

    override val layout: Int
        get() = R.layout.fragment_welcome
    override val screenName: Int
        get() = R.string.analytics_title_welcome_details

    override lateinit var viewModel: WelcomeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = getSharedViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContinueButton()
    }

    private fun setContinueButton() {
        fillFormButton.setOnClickListener {
            viewModel.nextToMain().call()
        }
    }
}
