package ploton.restapi_postgres_docker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ploton.restapi_postgres_docker.service.WalletService;

import javax.naming.InsufficientResourcesException;
import javax.swing.plaf.basic.BasicLabelUI;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping("")
    public ResponseEntity<UUID> walletAction(@RequestParam UUID walletID,
                                             @RequestParam String operationType,
                                             @RequestParam BigDecimal amount) {
        try {
            walletService.walletAction(walletID, operationType, amount);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(walletID);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(walletID);
        }
    }

    @GetMapping("")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/{walletID}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable("walletID") UUID walletID) {
        Optional<BigDecimal> balance = Optional.of(walletService.getBalance(walletID));
        return balance
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
