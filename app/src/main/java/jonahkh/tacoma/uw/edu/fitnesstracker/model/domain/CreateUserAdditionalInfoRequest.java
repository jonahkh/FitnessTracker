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
public class CreateUserAdditionalInfoRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String birthDay;
    private int weight;
    private int heightFt;
    private int heightIn;
    private char gender;
    private String activityLevel;
    private int daysToWorkout;
}
