package ro.code4.casefile.ui.login.codeverification

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentCodeVerificationBinding
import ro.code4.casefile.helper.AppExceptions
import ro.code4.casefile.helper.TextWatcherDelegate
import ro.code4.casefile.helper.hideKeyboard
import ro.code4.casefile.helper.isOnline
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.login.AuthenticationViewModel

class CodeVerificationFragment : ViewModelFragment<CodeVerificationViewModel>() {

    companion object {
        val TAG = CodeVerificationFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentCodeVerificationBinding
    private val baseViewModel: AuthenticationViewModel by activityViewModels()

    override val screenName: Int
        get() = R.string.analytics_title_code_verification
    override val layout: Int
        get() = R.layout.fragment_code_verification
    override val viewModel: CodeVerificationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.codeVerification.addTextChangedListener(object :
            TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.codeChanged(s.toString())
            }
        })

        binding.codeVerification.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.verifyCodeButton.isEnabled) {
                    activity?.hideKeyboard()
                    viewModel.verifyCode(binding.codeVerification.text.toString())
                    true
                } else false
            } else false
        }

        binding.resendCodeButton.setOnClickListener {
            viewModel.resendCode()
        }

        binding.verifyCodeButton.setOnClickListener {
            if (context?.isOnline() == false) {
                showDefaultErrorSnackbar(binding.verifyCodeButton, AppExceptions.NO_INTERNET)
                return@setOnClickListener
            }
            viewModel.verifyCode(binding.codeVerification.text.toString())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.codeLengthVerified()
            .observe(viewLifecycleOwner, Observer { isCodeLengthConfirmed ->
                binding.verifyCodeButton.isEnabled = isCodeLengthConfirmed
            })

        viewModel.codeVerified().observe(viewLifecycleOwner, Observer { result ->
            result.handle(
                onLoading = { baseViewModel.showProgressDialog() },
                onSuccess = {
                    baseViewModel.dismissProgressDialog()
                    baseViewModel.codeVerified()
                },
                onFailure = { exception ->
                    baseViewModel.dismissProgressDialog()
                    exception.message?.let { showErrorSnackbar(binding.verifyCodeButton, it) }
                        ?: showDefaultErrorSnackbar(
                            binding.verifyCodeButton,
                            AppExceptions.GENERIC_ERROR
                        )
                })
        })

        viewModel.codeResent().observe(viewLifecycleOwner, Observer { result ->
            result.handle(
                onLoading = { baseViewModel.showProgressDialog() },
                onSuccess = {
                    baseViewModel.dismissProgressDialog()
                    showErrorSnackbar(
                        binding.resendCodeButton,
                        getString(R.string.resend_code_confirmation)
                    )
                },
                onFailure = { exception ->
                    exception.message?.let { showErrorSnackbar(binding.verifyCodeButton, it) }
                        ?: showDefaultErrorSnackbar(
                            binding.verifyCodeButton,
                            AppExceptions.GENERIC_ERROR
                        )
                }
            )
        })
    }
}
