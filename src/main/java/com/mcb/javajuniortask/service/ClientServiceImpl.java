package com.mcb.javajuniortask.service;

import com.mcb.javajuniortask.dto.ClientDTO;
import com.mcb.javajuniortask.dto.TxnDTO;
import com.mcb.javajuniortask.model.Client;
import com.mcb.javajuniortask.model.Debt;
import com.mcb.javajuniortask.model.Txn;
import com.mcb.javajuniortask.repository.ClientRepository;
import com.mcb.javajuniortask.repository.TxnRepository;
import com.mcb.javajuniortask.service.exceptions.ClientNotFoundException;
import com.mcb.javajuniortask.service.exceptions.DebtNotFoundException;
import com.mcb.javajuniortask.service.exceptions.DebtOverpayException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final TxnRepository txnRepository;

    public ClientServiceImpl(ClientRepository clientRepository, TxnRepository txnRepository) {
        this.clientRepository = clientRepository;
        this.txnRepository = txnRepository;
    }

    @Override
    public Iterable<ClientDTO> getAllClients() {
        return StreamSupport.stream(clientRepository.findAll().spliterator(), false).map(client -> {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setName(client.getName());
            clientDTO.setTotalDebt(client.getDebts().stream().map(Debt::getValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
            clientDTO.setId(client.getId());
            return clientDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public UUID addClientOrReturnExisting(String name) {
        Optional<Client> any = StreamSupport.stream(clientRepository.findAll().spliterator(), false).filter(client -> client.getName().equals(name)).findAny();
        if(any.isPresent()){
            return any.get().getId();
        }
        Client client = new Client();
        client.setName(name);
        client.setId(UUID.randomUUID());
        client = clientRepository.save(client);
        return client.getId();
    }

    @Override
    public UUID addDebtToClient(UUID clientId, BigDecimal value) throws ClientNotFoundException {
        Client client = clientRepository.findOne(clientId);
        if(client == null){
            throw new ClientNotFoundException();
        }
        Debt debt = new Debt();
        debt.setValue(value);
        debt.setId(UUID.randomUUID());
        debt.setClient(client);
        client.getDebts().add(debt);
        clientRepository.save(client);
        return debt.getId();
    }

    @Override
    public void addPaymentToDebt(UUID clientId, UUID debtId, BigDecimal value) throws ClientNotFoundException, DebtNotFoundException, DebtOverpayException {
        Client client = clientRepository.findOne(clientId);
        if(client == null){
            throw new ClientNotFoundException();
        }
        Optional<Debt> debt = client.getDebts().stream().filter(d -> d.getId().equals(debtId)).findAny();
        if (!debt.isPresent()) {
            throw new DebtNotFoundException();
        }
        Debt existingDebt = debt.get();
        if (existingDebt.getValue().compareTo(value) < 0) {
            throw new DebtOverpayException();
        }
        Txn txn = new Txn();
        txn.setClient(client);
        txn.setDateTime(System.currentTimeMillis());
        txn.setDebt(existingDebt);
        txn.setValue(value);
        txn.setId(UUID.randomUUID());
        txnRepository.save(txn);
        existingDebt.setValue(existingDebt.getValue().subtract(value));
        clientRepository.save(client);
    }

    @Override
    public Iterable<TxnDTO> getAllTransactions() {
        return StreamSupport.stream(txnRepository.findAll().spliterator(), false).map(txn -> {
            TxnDTO txnDTO = new TxnDTO();
            txnDTO.setClientId(txn.getClient().getId());
            txnDTO.setDebtId(txn.getDebt().getId());
            txnDTO.setValue(txn.getValue());
            txnDTO.setDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(txn.getDateTime()), ZoneId.systemDefault()));
            txnDTO.setId(txn.getId());
            return txnDTO;
        }).collect(Collectors.toList());
    }
}
