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
public class StartWorkoutRequest {
    private int workoutNumber;
    private String workoutName;
    private String dateCompleted;
    private String email;
    private String type;
}
