package com.hospital.treatment.util;

import com.hospital.treatment.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure-Java parser (no Spring dependency, easy to unit test).
 * Format: PATIENTID-TEST:STATUS[,PATIENTID-TEST:STATUS...]
 * e.g.    1001-BLOOD:C,1002-XRAY:P,1003-SCAN:C,1001-BLOOD:P
 */
public final class TreatmentInputParser {

    /** One parsed (still unconverted) record. */
    public record RawRecord(Long id, String test, String statusCode) { }

    private TreatmentInputParser() { }

    public static List<RawRecord> parse(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidInputException("Input string must not be null or empty");
        }

        List<RawRecord> out = new ArrayList<>();
        String[] records = input.split(",", -1);          // -1 keeps empty tokens so "a,,b" is rejected

        for (int i = 0; i < records.length; i++) {
            String rec = records[i].trim();
            String where = "Record #" + (i + 1) + " ('" + rec + "')";

            if (rec.isEmpty()) {
                throw new InvalidInputException(where + ": record is empty");
            }

            int dash = rec.indexOf('-');
            int colon = rec.lastIndexOf(':');
            if (dash < 0 || colon < 0 || colon < dash) {
                throw new InvalidInputException(where + ": expected format ID-TEST:STATUS");
            }

            String idPart = rec.substring(0, dash).trim();
            String testPart = rec.substring(dash + 1, colon).trim();
            String statusPart = rec.substring(colon + 1).trim();

            if (idPart.isEmpty())     throw new InvalidInputException(where + ": Patient ID is missing");
            if (testPart.isEmpty())   throw new InvalidInputException(where + ": Test is missing");
            if (statusPart.isEmpty()) throw new InvalidInputException(where + ": Status is missing");

            long id;
            try {
                id = Long.parseLong(idPart);
            } catch (NumberFormatException e) {
                throw new InvalidInputException(where + ": Patient ID '" + idPart + "' is not a number");
            }
            if (id <= 0) {
                throw new InvalidInputException(where + ": Patient ID must be positive");
            }

            out.add(new RawRecord(id, testPart.toUpperCase(), statusPart.toUpperCase()));
        }
        return out;
    }
}
