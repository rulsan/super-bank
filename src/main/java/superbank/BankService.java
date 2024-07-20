package superbank;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import superbank.model.TransferBalance;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class BankService {

    private final BalanceRepository repository;

    public BigDecimal getBalance(Long accountId) {
        BigDecimal balance = repository.getBalanceForId(accountId);
        if (balance == null){
            throw new IllegalArgumentException();
        }
        return balance;
    }

    public BigDecimal addMoney(Long to, BigDecimal amount) {
        BigDecimal currentBalance = repository.getBalanceForId(to);
        if(currentBalance == null) {
            repository.save(to, amount);
            return amount;
        } else {
            BigDecimal updatedBalance = currentBalance.add(amount);
            repository.save(to, updatedBalance);
            return updatedBalance;
        }
    }

    public void makeTransfer(TransferBalance transferBalance) {
        BigDecimal fromBalance = repository.getBalanceForId(transferBalance.getFrom());
        BigDecimal toBalance = repository.getBalanceForId(transferBalance.getTo());
        if (fromBalance == null || toBalance == null) throw new IllegalArgumentException("No ids");
        if (transferBalance.getAmont().compareTo(fromBalance) > 0) throw new IllegalArgumentException("No money");
        BigDecimal updatedFromBalance = fromBalance.subtract(transferBalance.getAmont());
        BigDecimal updatedToBalance = toBalance.add(transferBalance.getAmont());
        repository.save(transferBalance.getFrom(), updatedFromBalance);
        repository.save(transferBalance.getTo(), updatedToBalance);
    }
}
