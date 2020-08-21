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
import ro.code4.casefile.databinding.MockItemPatientBinding
import ro.code4.casefile.testutils.DataBindingIdlingResourceRule
import ro.code4.casefile.ui.patient.addpatient.MaritalStatus
import ro.code4.casefile.ui.patient.addpatient.UiMaritalStatus
import ro.code4.casefile.ui.patient.model.PatientUiModel

class PatientItemTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(
        MockActivity::class.java, true, false
    )

    @get:Rule
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityTestRule)

    @Before
    fun setup() {
        MockActivity.layout = R.layout.mock_item_patient
        activityTestRule.launchActivity(Intent())
    }

    @Test
    fun testPatientItemVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemPatientBinding) {
            bind.itemPatient.patientUiModel =
                PatientUiModel(1,"Eremia Grigorescu", 32, UiMaritalStatus.MARRIED , "Galați", "Iași")
        }
        onView(withId(R.id.patientName)).check(matches(isDisplayed()))
        onView(withId(R.id.patientAge)).check(matches(isDisplayed()))
        onView(withId(R.id.patientMaritalStatus)).check(matches(isDisplayed()))
        onView(withId(R.id.patientCity)).check(matches(isDisplayed()))
        onView(withId(R.id.patientCounty)).check(matches(isDisplayed()))
    }

    @Test
    fun testPatientItem_dataBinding() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemPatientBinding) {
            bind.itemPatient.patientUiModel =
                PatientUiModel(1,"Eremia Grigorescu", 32, UiMaritalStatus.MARRIED, "Galați", "Iași")
        }
        onView(withId(R.id.patientName)).check(matches(withText("Eremia Grigorescu")))
        onView(withId(R.id.patientAge)).check(matches(withText("32")))
        onView(withId(R.id.patientMaritalStatus)).check(matches(withText("căsătorit(ă)")))
        onView(withId(R.id.patientCity)).check(matches(withText("Galați")))
        onView(withId(R.id.patientCounty)).check(matches(withText("Iași")))
    }
}
