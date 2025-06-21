package com.example.springcurrencies.service;

import com.example.springcurrencies.model.response.CurrencyResponse;
import com.example.springcurrencies.model.response.ValCurs;
import com.example.springcurrencies.model.response.Valute;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final RestTemplate restTemplate;
    private static final String URL = "https://www.cbar.az/currencies/";

    public List<Valute> getAllCurrencies(String date) {
        ValCurs valCurs = fetchCurrencyData(date);
        return valCurs.getValTypes().stream()
                .flatMap(vt -> vt.getValutes().stream())
                .toList();
    }

    public Valute getCurrencyByName(String code, String date) {
        return getAllCurrencies(date).stream()
                .filter(val -> val.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Currency not found: " + code));
    }

    public List<CurrencyResponse> getAllCurrenciesAsResponse(String date) {
        return getAllCurrencies(date).stream()
                .map(this::mapToCurrencyResponse)
                .toList();
    }

    public CurrencyResponse getCurrencyByNameAsResponse(String code, String date) {
        Valute valute = getCurrencyByName(code, date);
        return mapToCurrencyResponse(valute);
    }

    private CurrencyResponse mapToCurrencyResponse(Valute valute) {
        return new CurrencyResponse(
                valute.getCode(),
                valute.getName(),
                valute.getValue(),
                valute.getNominal()
        );
    }

    private ValCurs fetchCurrencyData(String date) {
        String formatDate = formatDate(date);
        String url = URL + formatDate + ".xml";
        System.out.println("Sorgu URL: " + url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String xml = response.getBody();

            if (xml == null || xml.isBlank()) {
                throw new RuntimeException("Currency emeliyyati zamani bos cavab..");
            }
            if (xml.toLowerCase().contains("<html")) {
                throw new RuntimeException("Cbar cavabi HTML geldi. Tarixe uygun XML movcud deyil: " + formatDate);
            }

            JAXBContext context = JAXBContext.newInstance(ValCurs.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (ValCurs) unmarshaller.unmarshal(new StringReader(xml));

        } catch (Exception e) {
            throw new RuntimeException("Parse emeliyyati zamani xeta: " + e.getMessage());
        }
    }


    private String formatDate(String date) {
        if (date == null || date.isBlank()) {
            return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        }
        return date;
    }
}
