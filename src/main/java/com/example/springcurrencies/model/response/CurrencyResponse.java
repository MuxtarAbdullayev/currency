package com.example.springcurrencies.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyResponse {
    private String code;
    private String name;
    private String value;
    private int nominal;
}
