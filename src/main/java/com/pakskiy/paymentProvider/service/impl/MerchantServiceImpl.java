package com.pakskiy.paymentProvider.service.impl;

import com.pakskiy.paymentProvider.dto.merchant.MerchantRequestDto;
import com.pakskiy.paymentProvider.dto.merchant.MerchantResponseDto;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.repository.MerchantRepository;
import com.pakskiy.paymentProvider.service.MerchantService;
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
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    final static DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public Mono<MerchantResponseDto> create(MerchantRequestDto request) {
        return merchantRepository.save(MerchantEntity.builder()
                        .login(request.getLogin())
                        .key(request.getKey())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status("ACTIVE")
                        .build())
                .map(el -> MerchantResponseDto.builder().id(el.getId()).login(el.getLogin()).key(el.getKey()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DuplicateKeyException) {
                        log.warn("ERR_SAVE_DUPLICATE {}", ex.getMessage(), ex);
                        return Mono.just(MerchantResponseDto.builder()
                                .errorCode("-1001").build());
                    } else if (ex instanceof DataAccessException) {
                        log.warn("ERR_SAVE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(MerchantResponseDto.builder()
                                .errorCode("-1002").build());
                    } else {
                        log.warn("ERR_SAVE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(MerchantResponseDto.builder()
                                .errorCode("-1003").build());
                    }
                });

    }

    public Mono<MerchantResponseDto> update(MerchantRequestDto request) {
        return merchantRepository.findById(request.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("Merchant not founded")))
                .flatMap(merchant -> {
                    merchant.setLogin(request.getLogin());
                    merchant.setKey(request.getKey());
                    merchant.setUpdatedAt(LocalDateTime.now());
                    return merchantRepository.save(merchant);
                }).map(el -> MerchantResponseDto.builder().id(el.getId()).key(el.getKey()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DataAccessException) {
                        log.warn("ERR_UPDATE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(MerchantResponseDto.builder()
                                .errorCode("-1004").build());
                    } else {
                        log.warn("ERR_UPDATE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(MerchantResponseDto.builder()
                                .errorCode("-1005").build());
                    }
                });

    }

    public Mono<MerchantResponseDto> get(Long id) {
        return merchantRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Merchant not founded")))
                .map(el -> MerchantResponseDto.builder()
                        .id(el.getId())
                        .login(el.getLogin())
                        .key(el.getKey())
                        .createdAt(el.getCreatedAt().format(ISO_FORMATTER))
                        .updatedAt(el.getUpdatedAt().format(ISO_FORMATTER))
                        .status(el.getStatus()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DataAccessException) {
                        log.warn("ERR_UPDATE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(MerchantResponseDto.builder()
                                .errorCode("-1007").build());
                    } else {
                        log.warn("ERR_UPDATE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(MerchantResponseDto.builder()
                                .errorCode("-1008").build());
                    }
                });
    }

    public Mono<MerchantEntity> findByToken(String token) {
        String pair = new String(Base64.decodeBase64(token.substring(6)));
        var credentials = pair.split(":");
        String login = credentials[0];
        String key = credentials[1];
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
