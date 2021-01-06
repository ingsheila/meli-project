package com.meli.project.meliproject.constants;

public class ConstantValues {

    public static final String BASE_ENDPOINT = "/v1/context-information";
    public static final String TRACE_ENDPOINT = "/trace";
    public static final String STATS_ENDPOINT = "/stats";

    public static final String BAD_REQUEST_CODE = "400";
    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String NOT_FOUND = "404";

    public static final String EMPTY_STRING = "";

    private ConstantValues() {
        throw new IllegalStateException("Utility class");
    }

    public static final class KeyProperty {

        private KeyProperty() {
            throw new IllegalStateException("Utility class");
        }

        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCESS_KEY = "access_key";
        public static final String SYMBOLS = "symbols";
        public static final String FORMAT = "format";
    }

    public static final Double LATITUDE_BA = -34.6083;
    public static final Double LONGITUDE_BA = -58.3712;
}
