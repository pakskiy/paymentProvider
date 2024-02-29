package com.pakskiy.paymentProvider;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@RequiredArgsConstructor
class PaymentProviderApplicationTests {
	private static final DockerImageName dockerImageName = DockerImageName.parse("postgres:12.15");

    @Container
    static PostgreSQLContainer<?> postgresqlContainer = (PostgreSQLContainer) new PostgreSQLContainer(dockerImageName)
            .withDatabaseName("paymentProvider")
            .withUsername("postgres")
            .withPassword("123456");

//    @DynamicPropertySource
//    static void postgresqlProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.r2dbc.url", () -> "r2dbc:tc:postgresql://"
//                + postgresqlContainer.getHost() + ":56432"
//                + "/" + postgresqlContainer.getDatabaseName());
//        registry.add("spring.r2dbc.username", () -> postgresqlContainer.getUsername());
//        registry.add("spring.r2dbc.password", () -> postgresqlContainer.getPassword());
//    }

    @BeforeAll
    static void beforeAll() {
        postgresqlContainer.start();
		System.setProperty("spring.r2dbc.url", "r2dbc:postgresql://localhost:" + postgresqlContainer.getFirstMappedPort() + "/paymentProvider");
		System.setProperty("spring.flyway.url", "jdbc:postgresql://localhost:" + postgresqlContainer.getFirstMappedPort() + "/paymentProvider");
    }

    @AfterAll
    static void afterAll() {
        postgresqlContainer.stop();
    }


	@Test
	void contextLoads() {
	}

}
