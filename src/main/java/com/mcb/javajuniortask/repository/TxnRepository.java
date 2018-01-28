package com.mcb.javajuniortask.repository;

import com.mcb.javajuniortask.model.Txn;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TxnRepository extends PagingAndSortingRepository<Txn, UUID> {
}