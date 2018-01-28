package com.mcb.javajuniortask.service;

import com.mcb.javajuniortask.dto.ClientDTO;
import com.mcb.javajuniortask.dto.TxnDTO;
import com.mcb.javajuniortask.service.exceptions.ClientNotFoundException;
import com.mcb.javajuniortask.service.exceptions.DebtNotFoundException;
import com.mcb.javajuniortask.service.exceptions.DebtOverpayException;

import java.math.BigDecimal;
import java.util.UUID;

public interface ClientService {

    Iterable<ClientDTO> getAllClients();

    UUID addClientOrReturnExisting(String name);

    UUID addDebtToClient(UUID clientId, BigDecimal value) throws ClientNotFoundException;

    void addPaymentToDebt(UUID clientId, UUID debtId, BigDecimal value)
            throws ClientNotFoundException, DebtNotFoundException, DebtOverpayException;

    Iterable<TxnDTO> getAllTransactions();
}