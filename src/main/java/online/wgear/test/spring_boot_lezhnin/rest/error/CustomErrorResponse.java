package online.wgear.test.spring_boot_lezhnin.rest.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
}
