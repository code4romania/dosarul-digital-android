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
import ro.code4.casefile.databinding.MockItemQuestionBinding
import ro.code4.casefile.testutils.DataBindingIdlingResourceRule
import ro.code4.casefile.ui.forms.model.QuestionUiModel

class QuestionItemTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(
        MockActivity::class.java, true, false
    )

    @get:Rule
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityTestRule)

    @Before
    fun setup() {
        MockActivity.layout = R.layout.mock_item_question
        activityTestRule.launchActivity(Intent())
    }

    @Test
    fun testQuestionItemVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemQuestionBinding) {
            bind.itemQuestion.questionUiModel = QuestionUiModel(" ", " ", 0, 0)
        }
        onView(withId(R.id.questionCode)).check(matches(isDisplayed()))
    }

    @Test
    fun testQuestionItemVisible_iconsVisible() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemQuestionBinding) {
            bind.itemQuestion.questionUiModel =
                QuestionUiModel(" ", " ", R.drawable.ic_note, R.drawable.ic_check)
        }
        onView(withId(R.id.noteIcon)).check(matches(isDisplayed()))
        onView(withId(R.id.syncIcon)).check(matches(isDisplayed()))
    }

    @Test
    fun testQuestionItem_dataBinding() {
        val bind = activityTestRule.activity.binding
        if (bind is MockItemQuestionBinding) {
            bind.itemQuestion.questionUiModel = QuestionUiModel("Bă", "Pune mâna și lucrează", 0, 0)
        }
        onView(withId(R.id.questionCode)).check(matches(withText("Bă")))
        onView(withId(R.id.question)).check(matches(withText("Pune mâna și lucrează")))
    }
}
