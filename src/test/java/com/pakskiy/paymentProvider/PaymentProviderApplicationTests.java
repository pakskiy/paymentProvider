package com.pakskiy.paymentProvider;

import com.pakskiy.paymentProvider.dto.account.AccountRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantResponseDto;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@RequiredArgsConstructor
@AutoConfigureWebTestClient
class PaymentProviderApplicationTests {

    private static final String authToken = "Basic dGVzdGxvZ2luMDE6c0lUa2h5VW5UU3NQbXVI";
    private static final String login = "testlogin01";
    private static final String key = "sITkhyUnTSsPmuH";
    private static final DockerImageName dockerImageName = DockerImageName.parse("postgres:12.15");
    @Autowired
    private WebTestClient webClient;

    @Container
    static PostgreSQLContainer<?> postgresqlContainer = (PostgreSQLContainer) new PostgreSQLContainer(dockerImageName)
            .withDatabaseName("paymentProvider")
            .withUsername("postgres")
            .withPassword("123456");

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
        merchantRequestDto.setLogin(login);
        merchantRequestDto.setKey(key);

        var resultResponse = webClient.post()
                .uri("/api/v1/merchants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(merchantRequestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MerchantResponseDto.class).returnResult()
                .getResponseBody();
        assert resultResponse != null;
        assertEquals(resultResponse.getLogin(), login);
        assertEquals(resultResponse.getKey(), key);
    }

    @Test
    void createAccount() {
        long depositAmount = 10000;
        long limitAmount = 1000;
        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setDepositAmount(depositAmount);
        accountRequestDto.setLimitAmount(limitAmount);

        var resultResponse = webClient.post()
                .uri("/api/v1/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authToken)
                .body(BodyInserters.fromValue(accountRequestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class).returnResult()
                .getResponseBody();

        assert resultResponse != null;
        assertEquals(resultResponse.getDepositAmount(), depositAmount);
        assertEquals(resultResponse.getLimitAmount(), limitAmount);
    }
}