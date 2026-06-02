package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WitnessIn {
    @NotEmpty(message = "name cannot be empty")
    private String name;

    @NotEmpty(message = "statement cannot be empty")
    private String statement;
    @NotNull(message = "reliability score cannot be null")

    @DecimalMin(value = "0.0", message = "reliability score minimum is 0.0")
    @DecimalMax(value = "1.0", message = "reliability score maximum is 1.0")
    private Double reliabilityScore;
}
