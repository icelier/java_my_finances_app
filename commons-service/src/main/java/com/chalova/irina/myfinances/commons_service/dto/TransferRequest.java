package com.chalova.irina.myfinances.commons_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class TransferRequest {
    @JsonProperty("sum")
    private final String sum;
    @JsonProperty("accountFromId")
    private final Long accountFromId;
    @JsonProperty("accountToId")
    private final Long accountToId;
}
