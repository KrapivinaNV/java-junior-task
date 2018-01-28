package com.mcb.javajuniortask.shell;

import com.mcb.javajuniortask.dto.ClientDTO;
import com.mcb.javajuniortask.dto.TxnDTO;
import com.mcb.javajuniortask.service.ClientService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@ShellComponent
public class ClientShellComponent {

    private final ClientService clientService;

    public ClientShellComponent(ClientService clientService) {
        this.clientService = clientService;
    }

    @ShellMethod("Shows all clients in db")
    @Transactional
    public Iterable<ClientDTO> showAllClients() {
        return clientService.getAllClients();
    }

    @ShellMethod("Adds client to db")
    @Transactional
    public UUID addClient(@ShellOption String name) {
        return clientService.addClientOrReturnExisting(name);
    }

    @ShellMethod("Adds debt to client")
    @Transactional
    public UUID addDebtToClient(@ShellOption UUID clientId, @ShellOption BigDecimal value) {
        return clientService.addDebtToClient(clientId, value);
    }

    @ShellMethod(value = "Pays for client's debt", key = "add-payment-to-debt")
    @Transactional
    public void addPaymentToDebt(@ShellOption UUID clientId, @ShellOption UUID debtId, @ShellOption BigDecimal value) {
        clientService.addPaymentToDebt(clientId, debtId, value);
    }

    @ShellMethod(value = "Show all transactions", key = "show-all-transactions")
    @Transactional
    public Iterable<TxnDTO> showAllTransactions() {
        return clientService.getAllTransactions();
    }

}

