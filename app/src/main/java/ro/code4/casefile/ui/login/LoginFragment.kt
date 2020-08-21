package ro.code4.casefile.ui.login

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import ro.code4.casefile.BuildConfig
import ro.code4.casefile.R
import ro.code4.casefile.databinding.FragmentLoginBinding
import ro.code4.casefile.helper.AppExceptions
import ro.code4.casefile.helper.TextWatcherDelegate
import ro.code4.casefile.helper.isOnline
import ro.code4.casefile.sync.CitiesAndCountiesWorker
import ro.code4.casefile.sync.PatientsWorker
import ro.code4.casefile.ui.base.ViewModelFragment

class LoginFragment : ViewModelFragment<LoginViewModel>() {

    companion object {
        val TAG = LoginFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentLoginBinding
    private val baseViewModel: AuthenticationViewModel by activityViewModels()

    override val screenName: Int
        get() = R.string.analytics_title_login
    override val layout: Int
        get() = R.layout.fragment_login
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.email.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onEmailChange(p0?.toString())
            }
        })

        binding.password.addTextChangedListener(object : TextWatcher by TextWatcherDelegate {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onPasswordChange(p0?.toString())
            }
        })

        binding.loginButton.setOnClickListener {
            if (context?.isOnline() == false) {
                showDefaultErrorSnackbar(binding.loginButton, AppExceptions.NO_INTERNET)
                return@setOnClickListener
            }
            performLogin()
        }

        binding.password.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLogin()
                true
            } else false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginUserObservable()

        viewModel.loginButtonState().observe(viewLifecycleOwner, Observer { isEnabled ->
            binding.loginButton.isEnabled = isEnabled
        })

        if (BuildConfig.DEBUG) {
            binding.email.setText(R.string.test_email)
            binding.password.setText(R.string.test_password)
            binding.loginButton.isEnabled = true
        }
    }

    private fun performLogin() {
        viewModel.login(binding.email.text.toString(), binding.password.text.toString())
    }

    private fun loginUserObservable() {
        viewModel.loggedIn().observe(viewLifecycleOwner, Observer {
            it.handle(
                onSuccess = { _ ->
                    baseViewModel.dismissProgressDialog()

                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val syncCitiesAndCounties: WorkRequest =
                        OneTimeWorkRequestBuilder<CitiesAndCountiesWorker>()
                            .setConstraints(constraints)
                            .build()

                    val syncPatients: WorkRequest =
                        OneTimeWorkRequestBuilder<PatientsWorker>()
                            .setConstraints(constraints)
                            .build()

                    WorkManager
                        .getInstance(requireContext())
                        .enqueue(syncCitiesAndCounties)

                    WorkManager
                        .getInstance(requireContext())
                        .enqueue(syncPatients)

                    baseViewModel.successfulLogin()
                },
                onFailure = { error ->
                    // TODO: Handle errors to show personalized messages for each one
                    try {
                        baseViewModel.dismissProgressDialog()
                    } catch (e: IllegalStateException) {
                        // ok, nevermind
                    }

                    showDefaultErrorSnackbar(binding.loginButton, error)
                },
                onLoading = {
                    baseViewModel.showProgressDialog()
                }
            )
        })
    }
}
