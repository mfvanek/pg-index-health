/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.connection.PoolAwarePgConnection;
import com.mfvanek.pg.index.health.logger.Exclusions;
import com.mfvanek.pg.index.health.logger.IndicesHealthLogger;
import com.mfvanek.pg.index.health.logger.SimpleHealthLogger;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactoryImpl;

public class TestApp {

    public static void main(String[] args) {
        // TODO Писать на какой хост пошли с запросом!
        // TODO Добавить урл для асинхронной реплики в Чекаутере
        // TODO Надо больше тестов

        final PgConnection pgConnection = PoolAwarePgConnection.of("jdbc:postgresql://vla-di1z5b4nx75yqonp.db.yandex.net:6432,market-checkouter-test01i.db.yandex.net:6432,market-checkouter-test01h.db.yandex.net:6432/market_checkouter_test?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require",
                "market_checkouter_ro", "za12Pqo83mcbd10rtyskadv", "jdbc:postgresql://vla-di1z5b4nx75yqonp.db.yandex.net:6432,market-checkouter-test01i.db.yandex.net:6432,market-checkouter-test01h.db.yandex.net:6432/market_checkouter_test?targetServerType=preferSlave&loadBalanceHosts=true&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require");
        final IndicesHealth indicesHealth = new IndicesHealthImpl(pgConnection, new IndexMaintenanceFactoryImpl());
        final IndicesHealthLogger logger = new SimpleHealthLogger(indicesHealth, Exclusions.empty());
        System.out.println();
        logger.logAll().forEach(System.out::println);
    }
}
