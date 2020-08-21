
package ro.code4.casefile

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ro.code4.casefile.databinding.MockPatientBarBinding
import ro.code4.casefile.testutils.DataBindingIdlingResourceRule
import ro.code4.casefile.ui.patientbar.BeneficiaryBarUiModel

class PatientBarTest {

    @get:Rule
    var activityTestRule = ActivityTestRule<MockActivity>(
        MockActivity::class.java, true, false)

    @get:Rule
    val databindingIdlingResourceRule = DataBindingIdlingResourceRule(activityTestRule)

    @Before
    fun setup() {
        MockActivity.layout = R.layout.mock_patient_bar
        activityTestRule.launchActivity(Intent())
    }

    @Test
    fun testFragmentVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockPatientBarBinding) {
            bind.patientBar.beneficiaryBarUiModel = BeneficiaryBarUiModel("sexy time", listener = {})
        }
        onView(withId(R.id.beneficiaryBarText)).check(matches(withText("sexy time")))
    }

    @Test
    fun testButtonClicked() {
        val bind = activityTestRule.activity.binding
        var clicked = false
        if (bind is MockPatientBarBinding) {
            bind.patientBar.beneficiaryBarUiModel = BeneficiaryBarUiModel("sexy time", listener = { clicked = true })
        }
        onView(withId(R.id.beneficiaryBarButton)).perform(click())

        assertTrue(clicked)
    }
}
