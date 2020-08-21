package ro.code4.casefile

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ro.code4.casefile.testutils.DataBindingIdlingResourceRule

class PatientListTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(
        MockActivity::class.java, true, false
    )

    @get:Rule
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityTestRule)

    @Before
    fun setup() {
        MockActivity.layout = R.layout.fragment_patient_list
        activityTestRule.launchActivity(Intent())
    }

    @Test
    fun testFragmentVisible() {
        onView(ViewMatchers.withId(R.id.empty_list_message))
            .check(ViewAssertions.matches(ViewMatchers.withText("Lista ta e goală")))
    }

    @Test
    fun testButtonText() {
        onView(ViewMatchers.withId(R.id.add_patient_button))
            .check(ViewAssertions.matches(ViewMatchers.withText("Adaugă pacient nou")))
    }
}
