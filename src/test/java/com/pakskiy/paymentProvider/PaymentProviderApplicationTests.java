package com.pakskiy.paymentProvider;

import com.pakskiy.paymentProvider.dto.merchant.MerchantRequestDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@RequiredArgsConstructor
@AutoConfigureWebTestClient
class PaymentProviderApplicationTests {
	private static final DockerImageName dockerImageName = DockerImageName.parse("postgres:12.15");
    @Autowired
    private WebTestClient webClient;

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
	void createMerchant() {
        MerchantRequestDto merchantRequestDto = new MerchantRequestDto();
        merchantRequestDto.setLogin("test1");
        merchantRequestDto.setKey("test1key");

        webClient.post()
                .uri("/api/v1/merchants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(merchantRequestDto))
                .exchange()
                .expectStatus().isOk();
	}


    @ExtendWith(SpringExtension.class)
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    public class GreetingRouterTest {
        @Autowired
        private WebTestClient webTestClient;

        @Test
        public void testHello() {
            webTestClient
                    .get()
                    .uri("/hello")
                    .accept(MediaType.TEXT_PLAIN)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(String.class)
                    .isEqualTo("Hello, Spring");
        }
    }
}
