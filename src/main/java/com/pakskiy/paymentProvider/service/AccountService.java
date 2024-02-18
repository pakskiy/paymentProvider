package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.account.AccountCreateRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountCreateResponseDto;
import com.pakskiy.paymentProvider.dto.account.AccountGetResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import com.pakskiy.paymentProvider.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.pakskiy.paymentProvider.service.MerchantService.ISO_FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final MerchantService merchantService;

    public Mono<AccountEntity> getByMerchantId(Long id) {
        return accountRepository.findByMerchantId(id)
                .doOnError(error -> log.error("Error occurred while finding entity with ID: {}", id, error))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Entity with ID {} not found", id);
                    return Mono.empty();
                }))
                .map
                        (account -> {
                            log.info("account {}", account);
                            return account;
                        });
//        return accountRepository.findByMerchantId(id);
    }

    @SneakyThrows
    public Mono<AccountCreateResponseDto> create(AccountCreateRequestDto request, String token) {
        return merchantService.getByToken(token)
                .switchIfEmpty(Mono.error(new RuntimeException("Merchant not founded")))
                .flatMap(merchant -> accountRepository.save(AccountEntity.builder().merchantId(merchant.getId())
                        .depositAmount(request.getDepositAmount())
                        .limitAmount(request.getLimitAmount()).isOverdraft(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()))
                .map(el -> AccountCreateResponseDto.builder().id(el.getId()).depositAmount(el.getDepositAmount())
                        .limitAmount(el.getLimitAmount()).build())
                .onErrorResume(ex -> {
                    if (ex instanceof DuplicateKeyException) {
                        log.warn("ERR_SAVE_DUPLICATE {}", ex.getMessage(), ex);
                        return Mono.just(AccountCreateResponseDto.builder()
                                .errorCode("-1001").build());
                    } else if (ex instanceof DataAccessException) {
                        log.warn("ERR_SAVE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(AccountCreateResponseDto.builder()
                                .errorCode("-1002").build());
                    } else {
                        log.warn("ERR_SAVE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(AccountCreateResponseDto.builder()
                                .errorCode("-1003").build());
                    }
                });

    }

    public Mono<AccountGetResponseDto> get(String token) {
        return merchantService.getByToken(token)
                .switchIfEmpty(Mono.error(new RuntimeException("Merchant not founded")))
                .flatMap(merchant -> accountRepository.findByMerchantId(merchant.getId()))
                .map(account -> AccountGetResponseDto.builder().id(account.getId()).merchantId(account.getMerchantId())
                        .depositAmount(account.getDepositAmount()).limitAmount(account.getLimitAmount())
                        .isOverdraft(account.getIsOverdraft())
                        .createdAt(account.getCreatedAt().format(ISO_FORMATTER))
                        .updatedAt(account.getUpdatedAt().format(ISO_FORMATTER))
                        .build())
                .onErrorResume(ex -> {
                    if (ex instanceof DataAccessException) {
                        log.warn("ERR_UPDATE_ACCESS {}", ex.getMessage(), ex);
                        return Mono.just(AccountGetResponseDto.builder()
                                .errorCode("-1007").build());
                    } else {
                        log.warn("ERR_UPDATE_COMMON {}", ex.getMessage(), ex);
                        return Mono.just(AccountGetResponseDto.builder()
                                .errorCode("-1008").build());
                    }
                });
    }

    public Mono<AccountEntity> update(AccountEntity account) {
        return accountRepository.save(account);
    }

    public Flux<AccountEntity> list() {
        return accountRepository.findAll()
                .collectList().flatMapMany(account -> {
                    if (account.isEmpty()) {
                        return Flux.empty();
                    } else {
                        return Flux.fromIterable(account);
                    }
                });
    }
}
