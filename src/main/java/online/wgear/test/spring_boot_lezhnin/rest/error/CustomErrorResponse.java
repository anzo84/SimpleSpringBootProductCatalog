package online.wgear.test.spring_boot_lezhnin.rest.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "Error response")
public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
}
