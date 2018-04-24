/*
 * TCSS 450 FitnessTracker
 * Jonah Howard
 * Hector Diaz
 */

package jonahkh.tacoma.uw.edu.fitnesstracker;


import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.Calendar;
import java.util.Random;

import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;

/**
 * Test class to test the app. NOTE: for the tests to pass it will
 * require the user to manually log into the app and click on keep
 * me log in.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class AddCardioWorkoutTest extends
        ActivityInstrumentationTestCase2<DashboardActivity> {

    /** The field used for testing. */
    private Solo solo;

    /** Constructor setup. */
    public AddCardioWorkoutTest() {
        super(DashboardActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    /** Method to test the Menu Drawer. */
    public void testDrawer(){
        solo.clickOnActionBarHomeButton();
        boolean textFound = solo.searchText("Logged Cardio Workouts");
        assertTrue("Menu Drawer not opened", textFound);
    }



    /** Method to test if the Logged Cardio add Item works. */
    public void testAddingLoggedCardioWorkout() {
        solo.clickOnScreen(85, 75);
        solo.clickOnText("Logged Cardio Workouts");
        solo.clickOnView(getActivity().findViewById(R.id.fab_cardio_workout));
        Random random = new Random();
        //Generate a course number
        String exerciseName = "Running" + (random.nextInt(1000) + 1);
        solo.enterText(0, exerciseName);
        solo.enterText(1, "" + (random.nextInt(1000) + 1));
        solo.clickOnButton("Save");
        boolean textFound = solo.searchText(exerciseName);
        assertTrue("Logged Cardio Workout fail", textFound);
    }

    /**
     * Method to test if adding a workout works and than to see if
     * you can find that workout in the logged weight workouts.
     */
    public void testAddWorkoutAndLoggedWeightWorkouts() {
        solo.clickOnScreen(85, 75);
        solo.clickOnText("Workout Templates");
        String exerciseName = "Legs";
        solo.clickOnText(exerciseName);
        solo.clickOnButton("Start");
        solo.clickOnText("calf raises");
        Random random = new Random();
        String weight1 = "" + random.nextInt(300) + 1;
        String set1 = "" + random.nextInt(15) + 1;
        solo.enterText(0, weight1);
        solo.enterText(1, set1);
        solo.clickOnText("Add Set");
        solo.clickOnText("pistol squat");
        String weFight2 = "" + random.nextInt(300) + 1;
        String set2 = "" + random.nextInt(15) + 1;
        solo.enterText(0, weight2);
        solo.enterText(1, set2);
        solo.clickOnText("Add Set");
        solo.clickOnText("End Workout");
        solo.clickOnText("Yes");

        // today's date
        Calendar calendar = Calendar.getInstance();
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        String date = day + "-" + month + "-" + year;
        solo.clickOnScreen(85, 75);
        solo.clickOnText("Logged Weight Workouts");
        boolean textfound1 = solo.searchText(exerciseName);
        boolean textfound2 = solo.searchText(date);
        assertTrue("Logged Workout fail", textfound1 && textfound2);
    }


}
