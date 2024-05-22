package com.pakskiy.paymentProvider.rest;

import com.pakskiy.paymentProvider.dto.merchant.MerchantRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantResponseDto;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantRestControllerV1 {
    private final MerchantService merchantService;

    @PostMapping(value = "/create")
    public Mono<ResponseEntity<MerchantResponseDto>> create(@RequestBody MerchantRequestDto merchantRequestDto) {
        return merchantService.create(merchantRequestDto)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @PostMapping(value = "/update")
    public Mono<ResponseEntity<MerchantResponseDto>> update(@RequestBody MerchantRequestDto merchantRequestDto) {
        return merchantService.update(merchantRequestDto)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/get/{id}")
    public Mono<ResponseEntity<MerchantResponseDto>> get(@PathVariable Long id) {
        return merchantService.get(id)
                .map(res -> (res.getErrorCode() == null ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res)));
    }

    @GetMapping(value = "/list")
    public Flux<MerchantEntity> list() {
        return merchantService.list();
    }
}