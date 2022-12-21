package com.example.mobile.plugin;

public enum Fields {
    AGE("Возраст"),
    ADDITIONAL_PHONE("Дополнительный номер телефона"),
    ADDITIONAL_EMAIL("Дополнительный Email"),
    COUNTRY("Страна"),
    PLACE_WORK("Место работы"),
    PLACE_STUDY("Место обучения");

    private final String field;

    Fields(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
