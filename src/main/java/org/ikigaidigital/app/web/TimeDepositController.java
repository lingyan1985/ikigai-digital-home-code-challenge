package org.ikigaidigital.app.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ikigaidigital.application.TimeDepositService;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.ports.TimeDepositRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-deposits")
@Tag(name = "Time Deposits")
public class TimeDepositController {

    private final TimeDepositService service;
    private final TimeDepositRepository repo;

    public TimeDepositController(TimeDepositService service, TimeDepositRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping
    @Operation(summary = "Get all time deposits with withdrawals")
    public List<TimeDepositResponse> getAll() {
        return service.getAll().stream().map(td ->
                new TimeDepositResponse(
                        td.getId(),
                        td.getPlanType().toLowerCase(), // or td.getPlanTypeName()
                        td.getBalance(),
                        td.getDays(),
                        repo.findWithdrawalsFor(td.getId()).stream()
                                .map(w -> new TimeDepositResponse.WithdrawalResponse(
                                        w.getId(),
                                        w.getAmount().setScale(2, java.math.RoundingMode.HALF_UP).doubleValue(),
                                        w.getDate()
                                ))
                                .toList()
                )
        ).toList();
    }

    @PostMapping("/recalculate")
    @Operation(summary = "Update balances of all time deposits (monthly interest)")
    public ResponseEntity<List<TimeDepositResponse>> recalc() {
        List<TimeDeposit> updated = service.recalculateAll();
        // Return the same representation as GET so clients can refresh in one call
        List<TimeDepositResponse> body = updated.stream().map(td ->
                new TimeDepositResponse(
                        td.getId(),
                        td.getPlanType(),
                        td.getBalance(),
                        td.getDays(),
                        repo.findWithdrawalsFor(td.getId()).stream()
                                .map(w ->
                                        new TimeDepositResponse.WithdrawalResponse(
                                                w.getId(),
                                                w.getAmount().setScale(2, java.math.RoundingMode.HALF_UP).doubleValue(),
                                                w.getDate()))
                                .toList()
                )
        ).toList();
        return ResponseEntity.ok(body);
    }
}
