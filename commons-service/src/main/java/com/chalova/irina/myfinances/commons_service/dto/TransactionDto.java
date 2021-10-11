package com.chalova.irina.myfinances.commons_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class TransactionDto {
    @JsonProperty("sum")
    private String sum;
    @JsonProperty("operation")
    private String operation;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("accountId")
    private Long accountId;
    @JsonProperty("categoryName")
    private String categoryName;
}
