package com.underground.invoiceservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    private Integer id;
    private String code;
    private String description;
    private double priceBuy;
    private double priceSell;
    private int stock;
}
