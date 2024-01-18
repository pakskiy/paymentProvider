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
        Mono<MerchantCreateResponseDto> result;
        try {
            result = merchantRepository.save(MerchantEntity.builder()
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
        } catch (Exception e) {
            log.error("ERR_USER_SERVICE", e);
            result = Mono.just(MerchantCreateResponseDto.builder().errorCode("-1003").build());
        }
        return result;
    }

    public Mono<MerchantUpdateResponseDto> update(MerchantUpdateRequestDto request) {
        Mono<MerchantUpdateResponseDto> result;

        try {
            result = merchantRepository.findById(request.getId()).flatMap(el ->
                            merchantRepository.save(MerchantEntity.builder()
                                    .id(el.getId())
                                    .login(el.getLogin())
                                    .key(request.getKey())
                                    .createdAt(el.getCreatedAt())
                                    .updatedAt(LocalDateTime.now())
                                    .status(el.getStatus())
                                    .build()))
                    .map(el -> MerchantUpdateResponseDto.builder().id(el.getId()).key(el.getKey()).build())
                    .switchIfEmpty(Mono.just(MerchantUpdateResponseDto.builder().errorCode("-1006").build()))
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
        } catch (Exception e) {
            log.error("ERR_USER_SERVICE", e);
            result = Mono.just(MerchantUpdateResponseDto.builder().errorCode("-1007").build());
        }
        return result;
    }

    public Mono<MerchantGetResponseDto> get(Long id) {
        Mono<MerchantGetResponseDto> result;

        try {
            result = merchantRepository.findById(id)
                    .map(el -> MerchantGetResponseDto.builder()
                            .id(el.getId())
                            .login(el.getLogin())
                            .key(el.getKey())
                            .createdAt(el.getCreatedAt().format(ISO_FORMATTER))
                            .updatedAt(el.getUpdatedAt().format(ISO_FORMATTER))
                            .status(el.getStatus()).build())
                    .switchIfEmpty(Mono.just(MerchantGetResponseDto.builder()
                            .errorCode("-1006").build()))
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
        } catch (Exception e) {
            log.error("ERR_USER_SERVICE", e);
            result = Mono.just(MerchantGetResponseDto.builder().errorCode("-1010").build());
        }
        return result;
    }

    public Mono<MerchantEntity> checkByToken(String token) {
        String pair = new String(Base64.decodeBase64(token.substring(6)));
        String login = pair.split(":")[0];
        String key = pair.split(":")[1];

        return merchantRepository.findByLoginAndKey(login, key);
    }
}
