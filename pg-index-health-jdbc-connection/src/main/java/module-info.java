module io.github.mfvanek.pg.connection {
    requires org.jspecify;
    requires io.github.mfvanek.pg.model;
    requires java.sql;
    requires org.apache.commons.dbcp2;

    exports io.github.mfvanek.pg.connection;
    exports io.github.mfvanek.pg.connection.factory;
    exports io.github.mfvanek.pg.connection.host;
    exports io.github.mfvanek.pg.connection.exception;
}
