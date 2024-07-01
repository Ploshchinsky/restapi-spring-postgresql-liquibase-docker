package ploton.restapi_postgres_docker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ploton.restapi_postgres_docker.model.Transaction;
import ploton.restapi_postgres_docker.model.Wallet;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class WalletService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletRepository walletRepository;

    public void walletAction(UUID walletID, String operationType, BigDecimal amount)
            throws InsufficientResourcesException {
        //Check the wallet exist and get its balance
        Wallet wallet = walletRepository
                .findByIdForUpdate(walletID)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found"));
        BigDecimal currentBalance = wallet.getBalance();
        Transaction transaction = new Transaction();
        transaction.setWalletId(walletID);
        transaction.setAmount(amount);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        //Identify operation type
        switch (operationType) {
            case "DEPOSIT":
                transaction.setOperationType(operationType);
                wallet.setBalance(currentBalance.add(amount));
                walletRepository.save(wallet);
                break;
            case "WITHDRAW":
                if (wallet.getBalance().compareTo(amount) > 0) {
                    transaction.setOperationType(operationType);
                    wallet.setBalance(currentBalance.subtract(amount));
                    walletRepository.save(wallet);
                } else {
                    throw new InsufficientResourcesException("Not enough cash for withdrawal");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid operation type");
        }
        transactionRepository.save(transaction);
    }

    public BigDecimal getBalance(UUID walletId) {
        Wallet wallet = walletRepository
                .findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found"));
        return wallet.getBalance();
    }
}
