package com.example.springcurrencies.controller;

import com.example.springcurrencies.model.response.CurrencyResponse;
import com.example.springcurrencies.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/getAllCurrencies")
    public ResponseEntity<List<CurrencyResponse>> getAllCurrencies(
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(currencyService.getAllCurrenciesAsResponse(date));
    }

    @GetMapping("/getCurrencyByName")
    public ResponseEntity<CurrencyResponse> getCurrencyByName(
            @RequestParam String code,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(currencyService.getCurrencyByNameAsResponse(code, date));
    }
}
