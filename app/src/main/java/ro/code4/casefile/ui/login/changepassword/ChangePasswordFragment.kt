package ro.code4.casefile.ui.login.changepassword

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.casefile.BuildConfig
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentChangePasswordBinding
import ro.code4.casefile.helper.AppExceptions
import ro.code4.casefile.helper.TextWatcherDelegate
import ro.code4.casefile.helper.hideKeyboard
import ro.code4.casefile.helper.isOnline
import ro.code4.casefile.ui.base.ViewModelFragment
import ro.code4.casefile.ui.login.AuthenticationViewModel

class ChangePasswordFragment : ViewModelFragment<ChangePasswordViewModel>() {

    companion object {
        val TAG = ChangePasswordFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentChangePasswordBinding
    private val baseViewModel: AuthenticationViewModel by activityViewModels()

    override val screenName: Int
        get() = R.string.analytics_title_change_password
    override val layout: Int
        get() = R.layout.fragment_change_password
    override val viewModel: ChangePasswordViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPassword.addTextChangedListener(object :
            TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNewPasswordChange(s?.toString())
            }
        })

        binding.retypePassword.addTextChangedListener(object :
            TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onRetypePasswordChange(s?.toString())
            }
        })

        binding.retypePassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.changePasswordButton.isEnabled) {
                    activity?.hideKeyboard()
                    viewModel.changePassword()
                    true
                } else false
            } else false
        }

        binding.changePasswordButton.setOnClickListener {
            if (context?.isOnline() == false) {
                showDefaultErrorSnackbar(binding.changePasswordButton, AppExceptions.NO_INTERNET)
                return@setOnClickListener
            }
            viewModel.changePassword()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.changePasswordButtonState().observe(viewLifecycleOwner,
            Observer { isSameLength ->
                binding.changePasswordButton.isEnabled = isSameLength
            })

        viewModel.passwordChanged().observe(viewLifecycleOwner, Observer { result ->
            result.handle(
                onLoading = { baseViewModel.showProgressDialog() },
                onSuccess = {
                    baseViewModel.dismissProgressDialog()
                    baseViewModel.passwordChanged()
                },
                onFailure = { exception ->
                    baseViewModel.dismissProgressDialog()
                    when (exception) {
                        ChangePasswordException.NOT_EQUAL_EXCEPTION -> {
                            Snackbar.make(
                                binding.changePasswordButton,
                                getString(R.string.change_password_not_equal),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        ChangePasswordException.FORMAT_EXCEPTION -> {
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle(R.string.change_password_alert_title)
                                setMessage(R.string.change_password_alert_message)
                                setPositiveButton(
                                    R.string.change_password_alert_button
                                ) { dialog, _ -> dialog?.dismiss() }
                            }.show()
                        }
                    }
                }
            )
        })

        if (BuildConfig.DEBUG) {
            binding.newPassword.setText(R.string.new_password)
            binding.retypePassword.setText(R.string.new_password_retype)
            binding.changePasswordButton.isEnabled = true
        }
    }
}
