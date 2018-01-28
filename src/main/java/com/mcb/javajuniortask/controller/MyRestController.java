package com.mcb.javajuniortask.controller;

import com.mcb.javajuniortask.dto.ClientDTO;
import com.mcb.javajuniortask.dto.TxnDTO;
import com.mcb.javajuniortask.error.Error;
import com.mcb.javajuniortask.error.ErrorCode;
import com.mcb.javajuniortask.service.ClientService;
import com.mcb.javajuniortask.service.exceptions.ClientNotFoundException;
import com.mcb.javajuniortask.service.exceptions.DebtNotFoundException;
import com.mcb.javajuniortask.service.exceptions.DebtOverpayException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
public class MyRestController {
    private ClientService clientService;

    public MyRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/client/{client-name}")
    public Response addClient(@PathVariable(name = "client-name") String clientName, HttpServletResponse response) {
        try {
            UUID clientUuid = clientService.addClientOrReturnExisting(clientName);
            return new Response<>(clientUuid);
        } catch (Exception e) {
            Error error = new Error(ErrorCode.OTHER, "an error has occurred during client creation");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Response<>(error);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/clients")
    public Response getAllClients(HttpServletResponse response) {
        try {
            Iterable<ClientDTO> allClients = clientService.getAllClients();
            return new Response<>(allClients);
        } catch (Exception e) {
            Error error = new Error(ErrorCode.OTHER, "an error has occurred during get client operation");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Response<>(error);
        }

    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/client/{client-id}/amount/{value}")
    public Response addDebtToClient(@PathVariable(name = "client-id") UUID clientId, @PathVariable(name = "value") BigDecimal value, HttpServletResponse response) {
        try {
            UUID uuid = clientService.addDebtToClient(clientId, value);
            return new Response<>(uuid);
        } catch (ClientNotFoundException e) {
            Error error = new Error(ErrorCode.CLIENT_NOT_FOUND, "an error has occurred during add debt to client operation");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new Response<>(error);
        } catch (Exception e) {
            Error error = new Error(ErrorCode.OTHER, "an error has occurred during add debt to client operation");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Response<>(error);
        }
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/client/{client-id}/debt/{debt-id}/amount/{value}")
    public Response addPaymentToDebt(@PathVariable(name = "client-id") UUID clientId, @PathVariable(name = "debt-id") UUID debtId, @PathVariable(name = "value") BigDecimal value, HttpServletResponse response) {
        try {
            clientService.addPaymentToDebt(clientId, debtId, value);
            return new Response();
        } catch (ClientNotFoundException e) {
            Error error = new Error(ErrorCode.CLIENT_NOT_FOUND, "an error has occurred during add payment to debt operation");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new Response<>(error);
        } catch (DebtNotFoundException e) {
            Error error = new Error(ErrorCode.DEBT_NOT_FOUND, "an error has occurred during add payment to debt operation");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new Response<>(error);
        } catch (DebtOverpayException e) {
            Error error = new Error(ErrorCode.DEBT_OVERPAY, "an error has occurred during add payment to debt operation");
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return new Response<>(error);
        } catch (Exception e) {
            Error error = new Error(ErrorCode.OTHER, "an error has occurred during add payment to debt operation");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Response<>(error);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/transactions")
    public Response getAllTransactions(HttpServletResponse response) {
        try {
            Iterable<TxnDTO> allTransactions = clientService.getAllTransactions();
            return new Response<>(allTransactions);
        } catch (Exception e) {
            Error error = new Error(ErrorCode.OTHER, "an error has occurred during add get all transactions operation");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Response<>(error);
        }
    }
}