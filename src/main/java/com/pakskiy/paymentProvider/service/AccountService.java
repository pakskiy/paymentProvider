package com.pakskiy.paymentProvider.service;

import com.pakskiy.paymentProvider.dto.account.AccountCreateRequestDto;
import com.pakskiy.paymentProvider.dto.account.AccountCreateResponseDto;
import com.pakskiy.paymentProvider.entity.AccountEntity;
import com.pakskiy.paymentProvider.entity.MerchantEntity;
import com.pakskiy.paymentProvider.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final MerchantService merchantService;

    public Optional<AccountEntity> getByMerchantId(Long id) {
        return Optional.ofNullable(accountRepository.findByMerchantId(id).toFuture().join());
    }

    public Mono<AccountEntity> save(AccountEntity accountEntity) {
        return accountRepository.save(accountEntity);
    }

    @SneakyThrows
    public Mono<AccountCreateResponseDto> create(AccountCreateRequestDto request, String token) {
        Optional<MerchantEntity> merchantEntityOptional = merchantService.getByToken(token);

        if (merchantEntityOptional.isPresent()) {
            return accountRepository.save(AccountEntity.builder().merchantId(merchantEntityOptional.get().getId())
                            .depositAmount(request.getDepositAmount())
                            .limitAmount(request.getLimitAmount()).isOverdraft(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build())
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
        } else {
            log.warn("ERR_CREATE_NOT_PRESENT");
            return Mono.just(AccountCreateResponseDto.builder()
                    .errorCode("-1001").build());
        }


    }

//    public Mono<AccountGetResponseDto> get(String token) {
//        Optional<MerchantEntity> merchantEntityOptional = merchantService.checkByToken(token);
//
//        Mono<AccountGetResponseDto> result;
//
//        try {
//            result = accountRepository.findByMerchantId(merchantEntityOptional.get().getId())
//                    .map(el -> AccountGetResponseDto.builder()
//                            .id(el.getId())
//                            .merchantId(el.getMerchantId())
//                            .depositAmount(el.getDepositAmount())
//                            .limitAmount(el.getLimitAmount())
//                            .isOverdraft(el.getIsOverdraft())
//                            .createdAt(el.getCreatedAt().format(ISO_FORMATTER))
//                            .updatedAt(el.getUpdatedAt().format(ISO_FORMATTER)).build())
//                    .switchIfEmpty(Mono.just(AccountGetResponseDto.builder()
//                            .errorCode("-1006").build()))
//                    .onErrorResume(ex -> {
//                        if (ex instanceof DataAccessException) {
//                            log.warn("ERR_UPDATE_ACCESS {}", ex.getMessage(), ex);
//                            return Mono.just(AccountGetResponseDto.builder()
//                                    .errorCode("-1007").build());
//                        } else {
//                            log.warn("ERR_UPDATE_COMMON {}", ex.getMessage(), ex);
//                            return Mono.just(AccountGetResponseDto.builder()
//                                    .errorCode("-1008").build());
//                        }
//                    });
//        } catch (Exception e) {
//            log.error("ERR_USER_SERVICE", e);
//            result = Mono.just(AccountGetResponseDto.builder().errorCode("-1010").build());
//        }
//        return result;
//    }
}
