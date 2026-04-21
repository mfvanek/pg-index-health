module io.github.mfvanek.pg.model.jackson3 {

    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    exports io.github.mfvanek.pg.model.jackson3;

    provides tools.jackson.databind.JacksonModule with
        io.github.mfvanek.pg.model.jackson3.PgIndexHealthModelModule;
}
