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
public class UpdateUserRequest {
    private String email;
    private int weight;
    private int daysToWorkout;
    private String activityLevel;
}
