package com.euodia.tax_calculator.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Énumération représentant les pays supportés pour le calcul des taxes
 */
@Getter
@RequiredArgsConstructor
public enum Country {
    US("United States"),
    CANADA("Canada"),
    FRANCE("France");

    private final String displayName;
}
