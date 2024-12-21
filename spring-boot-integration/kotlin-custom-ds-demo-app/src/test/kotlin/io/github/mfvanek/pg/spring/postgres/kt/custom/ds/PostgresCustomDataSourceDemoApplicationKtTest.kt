package io.github.mfvanek.pg.spring.postgres.kt.custom.ds

import com.zaxxer.hikari.HikariDataSource
import io.github.mfvanek.pg.connection.PgConnection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class PostgresCustomDataSourceDemoApplicationKtTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun contextLoadsAndContainsPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("pgihCustomDataSource"))
            .isInstanceOf(HikariDataSource::class.java)

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection::class.java)
    }
}
