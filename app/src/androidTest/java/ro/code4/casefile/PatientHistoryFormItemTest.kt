package ro.code4.casefile

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ro.code4.casefile.databinding.MockItemPatientHistoryFormBinding
import ro.code4.casefile.testutils.DataBindingIdlingResourceRule
import ro.code4.casefile.ui.patient.model.FormHistoryUiModel

class PatientHistoryFormItemTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(
        MockActivity::class.java, true, false
    )

    @get:Rule
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityTestRule)

    @Before
    fun setup() {
        MockActivity.layout = R.layout.mock_item_patient_history_form
        activityTestRule.launchActivity(Intent())
    }

    @Test
    fun testPatientHistoryFormItemVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemPatientHistoryFormBinding) {
            bind.itemPatientHistoryForm.patientFormHistoryUiModel =
                FormHistoryUiModel(
                    0,
                    " ",
                    " ",
                    0
                )
        }
        onView(withId(R.id.formTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun testPatientHistoryFormItemVisible_iconsVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemPatientHistoryFormBinding) {
            bind.itemPatientHistoryForm.patientFormHistoryUiModel =
                FormHistoryUiModel(
                    0,
                    " ",
                    " ",
                    R.drawable.ic_check
                )
        }
        onView(withId(R.id.syncIcon)).check(matches(isDisplayed()))
    }

    @Test
    fun testPatientHistoryFormItem_dataBinding() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemPatientHistoryFormBinding) {
            bind.itemPatientHistoryForm.patientFormHistoryUiModel =
                FormHistoryUiModel(
                    0,
                    "07.06.2020",
                    "Monitorizare sarcina - trimestrul I",
                    0
                )
        }
        onView(withId(R.id.formDate)).check(matches(withText("07.06.2020")))
        onView(withId(R.id.formTitle)).check(matches(withText("Monitorizare sarcina - trimestrul I")))
    }
}
