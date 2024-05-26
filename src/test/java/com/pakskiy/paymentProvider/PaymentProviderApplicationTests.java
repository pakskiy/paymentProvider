package com.pakskiy.paymentProvider;

import com.pakskiy.paymentProvider.dto.account.AccountRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantResponseDto;
import com.pakskiy.paymentProvider.service.AccountService;
import com.pakskiy.paymentProvider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@RequiredArgsConstructor
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentProviderApplicationTests {

    private static final String authToken = "Basic dGVzdGxvZ2luMDE6c0lUa2h5VW5UU3NQbXVI";
    private static final String login = "testlogin01";
    private static final String key = "sITkhyUnTSsPmuH";
    private static final DockerImageName dockerImageName = DockerImageName.parse("postgres:12.15");
    @Autowired
    private WebTestClient webClient;
    private static ServerWebExchange exchange;

    private final static MerchantRequestDto merchantRequestDto = new MerchantRequestDto();

    @Autowired
    AccountService accountService;

    @Autowired
    MerchantService merchantService;

    private long firstAccountId = 0L;
    private long firstMerchantId = 0L;

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
        merchantRequestDto.setLogin(login);
        merchantRequestDto.setKey(key);
        exchange = Mockito.mock(ServerWebExchange.class);
        when(exchange.getAttribute("merchantId")).thenReturn(1L);
        when(exchange.getAttribute("accountId")).thenReturn(1L);

    }

    @AfterAll
    static void afterAll() {
        postgresqlContainer.stop();
    }

    @Test
    @Order(1)
    void test_rest_create_merchant() {
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
        firstMerchantId = resultResponse.getId();
        assertTrue(firstMerchantId > 0);

    }

    @Test
    @Order(2)
    void test_rest_create_account() {
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
        firstAccountId = resultResponse.getId();
        assertTrue(firstAccountId > 0);
    }

    @Test
    @Order(3)
    void test_account_service_get_by_id_success() {
        long depositAmount = 10000;
        long limitAmount = 1000;
//        MerchantRequestDto newMerchantRequestDto = new MerchantRequestDto();
//        newMerchantRequestDto.setLogin("");
//        when(accountService.get(authToken)).thenReturn(Mono.just(merchantRequestDto));

//        AccountRequestDto account = new AccountRequestDto();
//        account.setDepositAmount(10000);
//        account.setLimitAmount(1000);

        Mono<AccountResponseDto> createdAccount = accountService.get(exchange);
        StepVerifier
                .create(createdAccount)
                .consumeNextWith(newAccount -> {
                    assertEquals(newAccount.getDepositAmount(), depositAmount);
                    assertEquals(newAccount.getLimitAmount(), limitAmount);
                }).verifyComplete();
    }

    @Test
    @Order(4)
    void test_account_service_get_by_id_fail() {
        long depositAmount = 10001;
        long limitAmount = 1001;
//        MerchantRequestDto newMerchantRequestDto = new MerchantRequestDto();
//        newMerchantRequestDto.setLogin("");
//        when(accountService.get(authToken)).thenReturn(Mono.just(merchantRequestDto));

//        AccountRequestDto account = new AccountRequestDto();
//        account.setDepositAmount(10000);
//        account.setLimitAmount(1000);

        Mono<AccountResponseDto> createdAccount = accountService.get(exchange);
        StepVerifier
                .create(createdAccount)
                .consumeNextWith(newAccount -> {
                    assertNotEquals(newAccount.getDepositAmount(), depositAmount);
                    assertNotEquals(newAccount.getLimitAmount(), limitAmount);
                }).verifyComplete();
    }

    @Test
    @Order(5)
    void test_merchant_get_success() {
        Mono<MerchantResponseDto> createdMerchant = merchantService.get(1L);
        StepVerifier
                .create(createdMerchant)
                .consumeNextWith(merchant -> {
                    assertEquals(merchant.getLogin(), login);
                    assertEquals(merchant.getKey(), key);
                }).verifyComplete();
    }

    @Test
    @Order(6)
    void test_merchant_get_fail() {
        Mono<MerchantResponseDto> createdMerchant = merchantService.get(1L);
        StepVerifier
                .create(createdMerchant)
                .consumeNextWith(merchant -> {
                    assertNotEquals(merchant.getLogin(), login + "wrong");
                    assertNotEquals(merchant.getKey(), key + "wrong");
                }).verifyComplete();
    }


}