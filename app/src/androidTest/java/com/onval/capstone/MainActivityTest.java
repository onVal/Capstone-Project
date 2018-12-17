package com.onval.capstone;

import com.onval.capstone.activities.MainActivity;
import com.onval.capstone.activities.RecordActivity;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> activityTestRule =
            new IntentsTestRule<>(MainActivity.class, false, false);

    @Test
    public void fabShouldOpenRecordActivity() {
        activityTestRule.launchActivity(null);

        onView(withId(R.id.main_fab))
                .perform(click());

        intended(hasComponent(RecordActivity.class.getName()));
        onView(withId(R.id.timer_tv)).check(matches(isDisplayed()));
    }
}
