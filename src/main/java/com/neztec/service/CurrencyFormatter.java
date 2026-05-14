package com.neztec.service;

import com.neztec.config.Settings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyFormatter {

    private final Settings settings;
    private final DecimalFormat decimalFormat;

    public CurrencyFormatter(Settings settings) {
        this.settings = settings;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(settings.getDecimalSeparator());
        symbols.setGroupingSeparator(settings.getThousandSeparator());

        decimalFormat = new DecimalFormat(settings.getCurrencyFormat(), symbols);
    }

    public String format(double amount) {
        String formatted = decimalFormat.format(amount);
        return settings.getCurrencySymbol() + formatted + settings.getCurrencySuffix();
    }

    public String formatShort(double amount) {
        if (amount >= 1_000_000) {
            return format(amount / 1_000_000) + "M";
        } else if (amount >= 1_000) {
            return format(amount / 1_000) + "K";
        }
        return format(amount);
    }
}