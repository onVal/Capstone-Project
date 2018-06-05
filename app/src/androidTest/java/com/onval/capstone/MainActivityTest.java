package com.onval.capstone;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
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
        onView(withId(R.id.timer)).check(matches(isDisplayed()));
    }

//    @Test
//    public void listElementShouldOpenCategoryFragment() {
//        activityTestRule.launchActivity(null);
//
//        onView(withId(R.id.main_fab))
//                .perform(click());
//    }
}
