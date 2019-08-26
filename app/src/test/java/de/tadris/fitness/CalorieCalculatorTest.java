package de.tadris.fitness;

import org.junit.Assert;
import org.junit.Test;

import de.tadris.fitness.data.Workout;
import de.tadris.fitness.util.CalorieCalculator;

public class CalorieCalculatorTest {

    @Test
    public void testCalculation(){
        Workout workout= new Workout();
        workout.avgSpeed= 2.7d;
        workout.workoutType= Workout.WORKOUT_TYPE_RUNNING;
        workout.duration= 1000L * 60 * 10;
        int calorie= CalorieCalculator.calculateCalories(workout, 80);
        System.out.println("Calories: " + calorie);
        Assert.assertEquals(120, calorie, 50);
    }

}
