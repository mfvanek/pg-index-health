/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mfvanek.pg;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainerProvider;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PreparedDbExtension implements BeforeAllCallback, AfterAllCallback {

    private volatile JdbcDatabaseContainer container;
    private volatile DataSource dataSource;

    private volatile List<Pair<String, String>> additionalParameters = new ArrayList<>();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        container = new PostgreSQLContainerProvider().newInstance();
        String[] startupCommand = Stream.concat(
                Arrays.stream(container.getCommandParts()),
                additionalParameters.stream()
                        .flatMap(kv -> Stream.of("-c", kv.getKey() + "=" + kv.getValue()))
        ).toArray(String[]::new);
        container.setCommand(startupCommand);
        container.start();

        dataSource = getDataSource();
    }

    @NotNull
    private BasicDataSource getDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(container.getJdbcUrl());
        basicDataSource.setUsername(container.getUsername());
        basicDataSource.setPassword(container.getPassword());
        basicDataSource.setDriverClassName(container.getDriverClassName());
        return basicDataSource;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        container.close();
    }

    public DataSource getTestDatabase() {
        if (dataSource == null) {
            throw new AssertionError("not initialized");
        }
        return dataSource;
    }

    public int getPort() {
        if (container == null) {
            throw new AssertionError("not initialized");
        }
        return container.getFirstMappedPort();
    }

    public PreparedDbExtension withAdditionalStartupParameter(String key, String value) {
        additionalParameters.add(Pair.of(key, value));
        return this;
    }
}
