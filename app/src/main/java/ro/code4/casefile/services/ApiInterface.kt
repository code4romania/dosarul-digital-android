package ro.code4.casefile.services

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ro.code4.casefile.data.model.City
import ro.code4.casefile.data.model.CodeVerification
import ro.code4.casefile.data.model.response.CodeVerificationResponse
import ro.code4.casefile.data.model.County
import ro.code4.casefile.data.model.Note
import ro.code4.casefile.data.model.PatientDetails
import ro.code4.casefile.data.model.ResponseAnswerContainer
import ro.code4.casefile.data.model.Section
import ro.code4.casefile.data.model.User
import ro.code4.casefile.data.model.response.FilledInAnswersResponse
import ro.code4.casefile.data.model.response.LoginResponse
import ro.code4.casefile.data.model.response.PatientsResponse
import ro.code4.casefile.data.model.response.ResendCodeVerificationResponse
import ro.code4.casefile.data.model.response.VersionResponse
import ro.code4.casefile.ui.patient.addpatient.AddUpdatePatientModel

interface ApiInterface {
    @POST("access/authorize")
    fun login(@Body user: User): Observable<LoginResponse>

    @POST("access/verify")
    fun verifyCode(@Body codeVerification: CodeVerification): Single<CodeVerificationResponse>

    @POST("access/resend")
    fun resendCode(): Single<ResendCodeVerificationResponse>

    @GET("/api/v1/form")
    fun getForms(): Observable<VersionResponse>

    @GET("/api/v1/form/{formId}")
    fun getForm(@Path("formId") formId: Int): Observable<List<Section>>

    @GET("/api/v1/beneficiary")
    fun getPatients(): Single<PatientsResponse>

    @GET("/api/v1/beneficiary/{id}")
    fun getPatientDetails(@Path("id") id: Int): Single<PatientDetails>

    @POST("/api/v1/beneficiary")
    fun savePatientDetails(@Body patientDetails: AddUpdatePatientModel): Single<Int>

    @PUT("/api/v1/beneficiary")
    fun updatePatientDetails(@Body patientDetails: AddUpdatePatientModel): Single<Boolean>

    @GET("/api/v1/county")
    fun getCounties(): Observable<List<County>>

    @GET("/api/v2/note")
    fun getNotes(
        @Query("BeneficiaryId") beneficiaryId: Int,
        @Query("FormId") formId: Int?
    ): Single<List<Note>>

    @GET("/api/v1/answers/filledIn")
    fun getRemoteAnswers(
        @Query("beneficiaryId") beneficiaryId: Int,
        @Query("formId") formId: Int
    ): Single<List<FilledInAnswersResponse>>

    @POST("/api/v1/answers")
    fun postQuestionAnswer(@Body responseAnswer: ResponseAnswerContainer): Single<ResponseBody>

    @POST("/api/v1/beneficiary/sendFile")
    @FormUrlEncoded
    fun sendFile(@Field("beneficiaryId") beneficiaryId: Int): Single<ResponseBody>

    @GET("/api/v1/county/{countyId}/cities")
    fun getCities(@Path("countyId") countyId: Int): Observable<List<City>>

    @Multipart
    @POST("/api/v2/note/upload")
    fun postNote(
        @Part file: MultipartBody.Part?,
        @Part beneficiaryId: MultipartBody.Part,
        @Part questionId: MultipartBody.Part,
        @Part description: MultipartBody.Part?
    ): Single<ResponseBody>
}
