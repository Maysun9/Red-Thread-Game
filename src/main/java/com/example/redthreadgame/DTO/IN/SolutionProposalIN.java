package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Check(constraints = "status IN ('FALSE', 'TRUE')")
public class SolutionProposalIN {

    @NotEmpty(message = "Reason is required")
    private String reason;

    @Pattern(regexp = "^(FALSE|TRUE)$", message = "Status must be FALSE or TRUE")
    private String status;

    @Min(value = 0, message = "Reject count must be 0 or more")
    private Integer rejectCount;

    @Min(value = 0, message = "Accept count must be 0 or more")
    private Integer acceptCount;
}
