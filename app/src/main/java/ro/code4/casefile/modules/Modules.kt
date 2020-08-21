package ro.code4.casefile.modules

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ro.code4.casefile.App
import ro.code4.casefile.BuildConfig.API_URL
import ro.code4.casefile.BuildConfig.DEBUG
import ro.code4.casefile.data.AppDatabase
import ro.code4.casefile.helper.DateDeserializer
import ro.code4.casefile.helper.RetrofitMaritalStatusSerializer
import ro.code4.casefile.helper.getToken
import ro.code4.casefile.repositories.Repository
import ro.code4.casefile.services.ApiInterface
import ro.code4.casefile.services.LoginManager
import ro.code4.casefile.services.LoginManagerImpl
import ro.code4.casefile.ui.forms.FormsViewModel
import ro.code4.casefile.ui.forms.filldate.FillDateViewModel
import ro.code4.casefile.ui.forms.questions.QuestionsDetailsViewModel
import ro.code4.casefile.ui.forms.questions.QuestionsViewModel
import ro.code4.casefile.ui.forms.selection.FormsSelectionViewModel
import ro.code4.casefile.ui.guide.GuideViewModel
import ro.code4.casefile.ui.login.AuthenticationViewModel
import ro.code4.casefile.ui.login.FirebaseAuthenticator
import ro.code4.casefile.ui.login.LoginViewModel
import ro.code4.casefile.ui.login.changepassword.ChangePasswordViewModel
import ro.code4.casefile.ui.login.codeverification.CodeVerificationViewModel
import ro.code4.casefile.ui.main.MainViewModel
import ro.code4.casefile.ui.notes.NoteViewModel
import ro.code4.casefile.ui.onboarding.OnboardingViewModel
import ro.code4.casefile.ui.patient.PatientDetailsViewModel
import ro.code4.casefile.ui.patient.addpatient.AddPatientViewModel
import ro.code4.casefile.ui.patient.addpatient.MaritalStatus
import ro.code4.casefile.ui.section.selection.PatientsViewModel
import ro.code4.casefile.ui.splashscreen.SplashScreenViewModel
import ro.code4.casefile.ui.welcome.WelcomeViewModel
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val gson: Gson by lazy {
    val gsonBuilder = GsonBuilder()
    gsonBuilder
        .registerTypeAdapter(MaritalStatus::class.java, RetrofitMaritalStatusSerializer())
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .excludeFieldsWithoutExposeAnnotation()
        .create()
}

val appModule = module {
    single { App.instance }
}

val loginModule = module {
    single<LoginManager> {
        LoginManagerImpl()
    }
}

val apiModule = module {
    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single {
        Interceptor { chain ->
            val original = chain.request()

            val token = get<SharedPreferences>().getToken()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .build()

            chain.proceed(request)
        }
    }

    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        interceptor
    }

    single {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(10, TimeUnit.SECONDS)
        httpClient.writeTimeout(10, TimeUnit.SECONDS)
        httpClient.connectTimeout(10, TimeUnit.SECONDS)
        get<Interceptor?>()?.let {
            httpClient.addInterceptor(it)
        }
        get<HttpLoggingInterceptor?>()?.let {
            httpClient.addInterceptor(it)
        }
        httpClient.build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(get<OkHttpClient>())
            .build()
    }
    single<Repository> {
        Repository()
//        MockRepository()
    }

    single<ApiInterface> {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(get<OkHttpClient>())
            .build().create(ApiInterface::class.java)
    }
}

val dbModule = module {
    single { AppDatabase.getDatabase(get()) }
    single { Executors.newSingleThreadExecutor() }
}

val viewModelsModule = module {
    viewModel { AuthenticationViewModel() }
    viewModel { LoginViewModel() }
    viewModel { CodeVerificationViewModel() }
    viewModel { ChangePasswordViewModel() }
    viewModel { OnboardingViewModel() }
    viewModel { MainViewModel() }
    viewModel { WelcomeViewModel() }
    viewModel { FormsViewModel() }
    viewModel { FormsSelectionViewModel() }
    viewModel { QuestionsViewModel() }
    viewModel { QuestionsDetailsViewModel() }
    viewModel { NoteViewModel() }
    viewModel { GuideViewModel() }
    viewModel { SplashScreenViewModel() }
    viewModel { FillDateViewModel() }
    viewModel { PatientsViewModel() }
    viewModel { PatientDetailsViewModel() }
    viewModel { AddPatientViewModel() }
}

val analyticsModule = module {
    single { FirebaseAnalytics.getInstance(get()) }
}

val firebaseModule = module {
    single { FirebaseAuthenticator() }
}
