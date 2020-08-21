package ro.code4.casefile.ui.guide

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_guide.*
import org.koin.android.ext.android.inject
import ro.code4.casefile.R
import ro.code4.casefile.helper.WebClient
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.widget.ProgressDialogFragment

class GuideFragment : ViewModelFragment<GuideViewModel>(), WebClient.WebLoaderListener {

    override val layout: Int
        get() = R.layout.fragment_guide
    override val screenName: Int
        get() = R.string.analytics_title_guide
    private val progressDialog: ProgressDialogFragment by lazy {
        ProgressDialogFragment().also {
            it.isCancelable = false
        }
    }
    override val viewModel: GuideViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebClient(this)
        viewModel.url().observe(viewLifecycleOwner, Observer {
            webView.loadUrl(it)
        })
    }

    override fun onPageFinished() {
        progressDialog.dismiss()
    }

    override fun onLoading() {
        if (!progressDialog.isResumed) {
            progressDialog.showNow(childFragmentManager, ProgressDialogFragment.TAG)
        }
    }

    override fun onDestroyView() {
        if (progressDialog.isResumed) {
            progressDialog.dismissAllowingStateLoss()
        }
        super.onDestroyView()
    }
}
