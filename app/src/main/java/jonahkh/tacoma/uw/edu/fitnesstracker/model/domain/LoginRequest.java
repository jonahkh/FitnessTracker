package jonahkh.tacoma.uw.edu.fitnesstracker.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jonah on 4/21/2018.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
