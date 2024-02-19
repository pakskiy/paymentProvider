package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.merchant.MerchantCreateRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantCreateResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantGetResponseDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantUpdateRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantUpdateResponseDto;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;
    final static DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public Mono<MerchantCreateResponseDto> create(MerchantCreateRequestDto request) {
        return merchantRepository.save(MerchantEntity.builder()
                        .login(request.getLogin())
                        .key(request.getKey())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status("ACTIVE")
                        .build())
                .map(el -> MerchantCreateResponseDto.builder().id(el.getId()).login(el.getLogin()).key(el.getKey()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DuplicateKeyException) {
                        log.warn("ERR_SAVE_DUPLICATE {}", ex.getMessage(), ex);
                        return Mono.just(MerchantCreateResponseDto.builder()
                                .errorCode("-1001").build());
                    } else if (ex instanceof DataAccessException) {
                        log.warn("ERR_SAVE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(MerchantCreateResponseDto.builder()
                                .errorCode("-1002").build());
                    } else {
                        log.warn("ERR_SAVE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(MerchantCreateResponseDto.builder()
                                .errorCode("-1003").build());
                    }
                });

    }

    public Mono<MerchantUpdateResponseDto> update(MerchantUpdateRequestDto request) {
        return merchantRepository.findById(request.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("Merchant not founded")))
                .flatMap(merchant -> {
                    merchant.setLogin(request.getLogin());
                    merchant.setKey(request.getKey());
                    merchant.setUpdatedAt(LocalDateTime.now());
                    return merchantRepository.save(merchant);
                }).map(el -> MerchantUpdateResponseDto.builder().id(el.getId()).key(el.getKey()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DataAccessException) {
                        log.warn("ERR_UPDATE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(MerchantUpdateResponseDto.builder()
                                .errorCode("-1004").build());
                    } else {
                        log.warn("ERR_UPDATE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(MerchantUpdateResponseDto.builder()
                                .errorCode("-1005").build());
                    }
                });

    }

    public Mono<MerchantGetResponseDto> get(Long id) {
        return merchantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Merchant not founded")))
                .map(el -> MerchantGetResponseDto.builder()
                        .id(el.getId())
                        .login(el.getLogin())
                        .key(el.getKey())
                        .createdAt(el.getCreatedAt().format(ISO_FORMATTER))
                        .updatedAt(el.getUpdatedAt().format(ISO_FORMATTER))
                        .status(el.getStatus()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DataAccessException) {
                        log.warn("ERR_UPDATE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(MerchantGetResponseDto.builder()
                                .errorCode("-1007").build());
                    } else {
                        log.warn("ERR_UPDATE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(MerchantGetResponseDto.builder()
                                .errorCode("-1008").build());
                    }
                });
    }

    public Mono<MerchantEntity> getByToken(String token) {
        String pair = new String(Base64.decodeBase64(token.substring(6)));
        String login = pair.split(":")[0];
        String key = pair.split(":")[1];

        return merchantRepository.findByLoginAndKey(login, key);
    }


    public Flux<MerchantEntity> list() {
        return merchantRepository.findAll()
                .collectList().flatMapMany(merchant -> {
                    if (merchant.isEmpty()) {
                        return Flux.empty();
                    } else {
                        return Flux.fromIterable(merchant);
                    }
                });
    }
}
