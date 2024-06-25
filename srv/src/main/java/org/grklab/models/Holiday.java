package org.grklab.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Holiday {
    @JsonProperty("tradingDate")
    private String tradingDate;

    @JsonProperty("weekDay")
    private String weekDay;

    @JsonProperty("description")
    private String description;

    @JsonProperty("Sr_no")
    private int srNo;
}