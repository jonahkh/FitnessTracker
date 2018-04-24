package jonahkh.tacoma.uw.edu.fitnesstracker.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jonah on 4/22/2018.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseRequest {

    private int setNumber;
    private int workoutNumber;
    private String currentExercise;
    private String reps;
    private String email;
    private String weight;

}
