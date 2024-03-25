package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.repository.MerchantRepository;
import com.pakskiy.paymentProvider.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = MerchantRestControllerV1.class)
@AutoConfigureWebTestClient
class MerchantRestControllerV1Test {
//    @Autowired
//    private WebTestClient webTestClient;
//    @Mock
//    MerchantRepository merchantRepository;
//
//    @Mock
//    MessageSource messageSource;
//
//    @MockBean
//    private final MerchantServiceImpl merchantServiceImpl;
//
//    MerchantRestControllerV1Test(MerchantServiceImpl merchantServiceImpl) {
//        this.merchantServiceImpl = merchantServiceImpl;
//    }
////    @InjectMocks
////    MerchantRestControllerV1 merchantRestControllerV1;
//
//    @Test
//    void handleCreateMerchant_ReturnValidResponseEntity(){
////        MerchantRequestDto merchantRequestDto = new MerchantRequestDto();
////        merchantRequestDto.setLogin("test1");
////        merchantRequestDto.setKey("test1key");
////
////        //when
////        this.merchantRestControllerV1.create(merchantRequestDto)
////                .map(el -> {
////                    if(el.getStatusCode().is2xxSuccessful()) {
////
////                    }
////                    return el;
////                });
//        webTestClient.get()
//                .uri("/create")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class).isEqualTo("Hello, World!");
//    }
}