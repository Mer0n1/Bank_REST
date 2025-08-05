package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.security.PersonDetails;
import com.example.bankcards.service.CardBlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.BindingValidatorUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private CardBlockRequestService cardBlockRequestService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllCards")
    public ResponseEntity<List<CardDto>> getAllCards() {
        return ResponseEntity.ok().body(cardService.getAllCards());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createCard")
    public ResponseEntity<?> createCard(@RequestBody @Valid CreateCardRequest request,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        Card card = cardService.createCard(request);

        log.info("Card created {}", request.getOwnerId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/blockCard")
    public ResponseEntity<?> blockCard(@RequestBody @Valid CardRequest request,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        cardService.changeCardStatus(request.getCardId(), Card.CardStatus.BLOCKED);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activateCard")
    public ResponseEntity<?> activateCard(@RequestBody @Valid CardRequest request,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        cardService.changeCardStatus(request.getCardId(), Card.CardStatus.ACTIVE);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/expireCard")
    public ResponseEntity<?> expireCard(@RequestBody @Valid CardRequest request,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        cardService.changeCardStatus(request.getCardId(), Card.CardStatus.EXPIRED);

        return ResponseEntity.ok().build();
    }


    /** User API --------------------------------------------------- */

    /** Упрощенная версия без Pageable */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myCards")
    public ResponseEntity<List<CardDto>> getMyCards(@AuthenticationPrincipal PersonDetails personDetails) {
        return ResponseEntity.ok(cardService.getCardsForCurrentUser(personDetails));
    }

    /** С участием Pageable */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myCardsFilterExample")
    public ResponseEntity<Page<CardDto>> getMyCards(@AuthenticationPrincipal PersonDetails personDetails,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size,
                                                    @RequestParam(defaultValue = "balance") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(cardService.getCardsForCurrentUser(personDetails, pageable));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/transferBetweenCards")
    public ResponseEntity<?> transferBetweenCards(@RequestBody @Valid TransferBetweenCardsRequest request,
                                                  BindingResult bindingResult,
                                                  @AuthenticationPrincipal PersonDetails personDetails) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        cardService.transferBetweenCards(request, personDetails);

        return ResponseEntity.ok().build();
    }

    /** CardBlockReq API --------------------------------------------------- */

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/sendCardBlockRequest")
    public ResponseEntity<?> sendCardBlockRequest(@RequestBody @Valid BlockCardRequest request,
                                                  BindingResult bindingResult,
                                                  @AuthenticationPrincipal PersonDetails personDetails) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        cardBlockRequestService.registerRequest(personDetails, request);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getCardBlockRequests")
    public ResponseEntity<List<CardBlockReqDto>> getCardBlockRequests() {
        return ResponseEntity.ok().body(cardBlockRequestService.getAllCardBlockReq());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/changeStatusCardBlockRequest")
    public ResponseEntity<?> changeStatusCardBlockRequest(@RequestBody @Valid ChangeStatusCardBlockReq req,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(BindingValidatorUtil.getErrors(bindingResult));

        cardBlockRequestService.changeStatusCardBlockRequest(req);

        return ResponseEntity.ok().build();
    }
}
