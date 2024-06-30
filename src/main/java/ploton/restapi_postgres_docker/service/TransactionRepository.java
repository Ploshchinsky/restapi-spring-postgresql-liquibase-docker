package ploton.restapi_postgres_docker.service;

import org.springframework.data.repository.CrudRepository;
import ploton.restapi_postgres_docker.model.Transaction;

import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
}
