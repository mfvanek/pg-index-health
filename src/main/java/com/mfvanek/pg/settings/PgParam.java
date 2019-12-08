package com.mfvanek.pg.settings;

import com.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public class PgParam {

    private final String name;
    private final String value;

    private PgParam(@Nonnull final String name, @Nonnull final String value) {
        this.name = Validators.notBlank(name, "name");
        this.value = Validators.notBlank(value, "value");
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    @Override
    @Nonnull
    public String toString() {
        return PgParam.class.getSimpleName() + '{' +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static PgParam of(@Nonnull final String name, @Nonnull final String value) {
        return new PgParam(name, value);
    }
}
