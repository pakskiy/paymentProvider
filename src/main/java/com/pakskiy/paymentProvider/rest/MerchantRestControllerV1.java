package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.dto.merchant.MerchantCreateRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantCreateResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantGetResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantUpdateRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantUpdateResponseDto;
import com.pakskiy.paymentProvider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantRestControllerV1 {
    private final MerchantService merchantService;

    @PostMapping(value = "/create")
    public Mono<ResponseEntity<MerchantCreateResponseDto>> create(@RequestBody MerchantCreateRequestDto merchantCreateRequestDto) {
        return merchantService.create(merchantCreateRequestDto)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @PostMapping(value = "/update")
    public Mono<ResponseEntity<MerchantUpdateResponseDto>> update(@RequestBody MerchantUpdateRequestDto merchantUpdateRequestDto) {
        return merchantService.update(merchantUpdateRequestDto)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/get/{id}")
    public Mono<ResponseEntity<MerchantGetResponseDto>> get(@PathVariable Long id) {
        return merchantService.get(id)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }
}