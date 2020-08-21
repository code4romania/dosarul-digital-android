
package ro.code4.casefile.ui.forms.filldate

import android.util.EventLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.parceler.Parcels
import ro.code4.casefile.data.model.PatientForm
import ro.code4.casefile.helper.Constants
import ro.code4.casefile.helper.SingleLiveEvent
import ro.code4.casefile.ui.base.BaseFormViewModel
import ro.code4.casefile.ui.base.BaseViewModel
import ro.code4.casefile.ui.forms.model.SelectedFormInfo
import java.util.*

class FillDateViewModel: BaseFormViewModel() {

    private val uiChange = MutableLiveData<Date>()
    private val navigationLiveData = SingleLiveEvent<SelectedFormInfo>()
    private lateinit var form: PatientForm
    val uiSubject: LiveData<Date> = uiChange

    private var selectedFormInfo: SelectedFormInfo? = null
    fun continueObserver(): LiveData<SelectedFormInfo> = navigationLiveData

    var dateSelected: Calendar = Calendar.getInstance()
    set(value) {
        field = value
        uiChange.postValue(value.time)
        this.form.completionDate = value.time
        this.selectedFormInfo?.completionDate = value.time
    }

    fun setData(selectedFormInfo: SelectedFormInfo) {
        if(selectedFormInfo.completionDate == null) selectedFormInfo.completionDate = Calendar.getInstance().time
        this.selectedFormInfo = selectedFormInfo
        disposables.add(repository.getPatientForm(selectedFormInfo.formId, selectedFormInfo.beneficiaryId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { form ->
            this.form = form
               val comDate = Calendar.getInstance()
                comDate.time = form.completionDate ?: Calendar.getInstance().time
                this.dateSelected = comDate

        })
        uiChange.value = selectedFormInfo.completionDate
        setTitle(selectedFormInfo.title)
    }

    fun continueSelected() {
        disposables.add(repository.savePatientForm(form).subscribe {
            result ->
            navigationLiveData.postValue(selectedFormInfo)
        })

    }

}
