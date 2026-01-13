package co.com.bancolombia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithBranchResponse {
    private String id;
    private String name;
    private Integer stock;
    private String branchId;
    private String branchName;
}
