package ro.code4.casefile

import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ro.code4.casefile.databinding.MockPatientDetailsFragmentBinding
import ro.code4.casefile.testutils.DataBindingIdlingResourceRule
import ro.code4.casefile.ui.patient.PatientDetailsListeners
import ro.code4.casefile.ui.patient.addpatient.UiMaritalStatus
import ro.code4.casefile.ui.patient.model.PatientUiModel

class PatientDetailsFragmentTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(
        MockActivity::class.java, true, false
    )

    @get:Rule
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityTestRule)

    @Before
    fun setup() {
        MockActivity.layout = R.layout.mock_patient_details_fragment
        activityTestRule.launchActivity(Intent())
    }

    @Test
    fun testPatientItemVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockPatientDetailsFragmentBinding) {
            bind.patientDetailsFragment.patientUiModel =
                PatientUiModel(1,"Eremia Grigorescu", 32, UiMaritalStatus.MARRIED, "Galați", "Iași")
        }
        //TODO forms to be linked to the fragment and check emptyUiVisibility vs formsHistory
        onView(withId(R.id.patientName)).check(matches(isDisplayed()))
        onView(withId(R.id.patientAge)).check(matches(isDisplayed()))
        onView(withId(R.id.patientMaritalStatus)).check(matches(isDisplayed()))
        onView(withId(R.id.patientCity)).check(matches(isDisplayed()))
        onView(withId(R.id.patientCounty)).check(matches(isDisplayed()))

        onView(withId(R.id.noFormHistoryGroup)).check(matches(isDisplayed()))
    }

    @Test
    fun testNoPatientFormHistoryLayoutVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockPatientDetailsFragmentBinding) {
            bind.patientDetailsFragment.noPatientFormHistoryLayoutVisibility = View.VISIBLE
            bind.patientDetailsFragment.patientFormHistoryListVisibility = View.GONE
        }

        onView(withId(R.id.noFormHistoryGroup)).check(matches(isDisplayed()))
    }

    @Test
    fun testPatientFormHistoryListVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockPatientDetailsFragmentBinding) {
            bind.patientDetailsFragment.noPatientFormHistoryLayoutVisibility = View.GONE
            bind.patientDetailsFragment.patientFormHistoryListVisibility = View.VISIBLE
        }

        onView(withId(R.id.formHistoryList)).check(matches(isDisplayed()))
    }

    @Test
    fun testPatientItem_dataBinding() {
        val bind = activityTestRule.activity.binding
        if (bind is MockPatientDetailsFragmentBinding) {
            bind.patientDetailsFragment.patientUiModel =
                PatientUiModel(1,"Eremia Grigorescu", 32, UiMaritalStatus.MARRIED, "Galați", "Iași")
        }

        onView(withId(R.id.patientName)).check(matches(withText("Eremia Grigorescu")))
        onView(withId(R.id.patientAge)).check(matches(withText("32")))
        onView(withId(R.id.patientMaritalStatus)).check(matches(withText("căsătorit(ă)")))
        onView(withId(R.id.patientCity)).check(matches(withText("Galați")))
        onView(withId(R.id.patientCounty)).check(matches(withText("Iași")))
    }

    @Test
    fun testPatientItem_dataBinding_listeners() {
        val bind = activityTestRule.activity.binding
        var editPatientClicked = false
        var patientFormsClicked = false
        var sendRecordClicked = false
        var addFamilyMemberClicked = false
        var addNoteClicked = false

        if (bind is MockPatientDetailsFragmentBinding) {
            bind.patientDetailsFragment.patientUiModel =
                PatientUiModel(1,"Eremia Grigorescu", 32, UiMaritalStatus.MARRIED, "Galați", "Iași")
            bind.patientDetailsFragment.patientDetailsListeners = PatientDetailsListeners(
                editPatient = { editPatientClicked = true },
                patientForms = { patientFormsClicked = true },
                sendRecord = { sendRecordClicked = true },
                addFamilyMember = { addFamilyMemberClicked = true },
                addNote = { addNoteClicked = true }
            )
        }

        onView(withId(R.id.editPatientButton)).perform(ViewActions.click())
        onView(withId(R.id.formButton)).perform(ViewActions.click())
        onView(withId(R.id.sendRecordButton)).perform(ViewActions.click())
        onView(withId(R.id.addFamilyMemberButton)).perform(ViewActions.click())
        onView(withId(R.id.addNoteButton)).perform(ViewActions.click())

        Assert.assertTrue(editPatientClicked)
        Assert.assertTrue(patientFormsClicked)
        Assert.assertTrue(sendRecordClicked)
        Assert.assertTrue(addFamilyMemberClicked)
        Assert.assertTrue(addNoteClicked)
    }
}
