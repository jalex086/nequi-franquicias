package co.com.bancolombia.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductStockRequest {
    @NotNull(message = "El stock es requerido")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    @JsonProperty("stock")
    private Integer stock;
}
