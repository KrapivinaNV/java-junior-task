package com.mcb.javajuniortask.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TxnDTO {
    private UUID id;
    private UUID clientId;
    private UUID debtId;
    private LocalDateTime dateTime;
    private BigDecimal value;
}
