package co.com.bancolombia.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFranchiseNameRequest {
    @NotBlank(message = "El nombre de la franquicia es obligatorio")
    private String name;
}
