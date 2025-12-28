package no.ntnu.folk.adamzk.repository;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;

/**
 * Constants and configuration for the Basic Register CSV files from Fimea.
 * https://fimea.fi/en/databases_and_registers/basic-register
 */
public final class BasicRegister {

    private BasicRegister() {
        // Utility class
    }

    /**
     * Character encoding used in Basic Register CSV files.
     */
    public static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    /**
     * Standard CSV format for Basic Register files.
     * Uses semicolon delimiter with header row.
     */
    public static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT
            .builder()
            .setDelimiter(';')
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .build();

    /**
     * Column indices for the medicine packages file.
     */
    public static final class Packages {

        private Packages() {
            // Utility class
        }

        /** PAKKAUSNRO - Package number */
        public static final int COL_PACKAGE_NUMBER = 0;

        /** VNRNRO - VNR number */
        public static final int COL_VNR = 1;

        /** LAAKENIMI - Medicine name */
        public static final int COL_MEDICINE_NAME = 11;

        /** VAHVUUS - Strength */
        public static final int COL_STRENGTH = 12;

        /** LAAKEMUOTONIMI - Dose form name */
        public static final int COL_DOSE_FORM = 13;

        /** HALTIJA - Marketing authorization holder */
        public static final int COL_PRODUCER = 15;

        /** ATCKOODI - ATC code */
        public static final int COL_ATC_CODE = 20;
    }

}
