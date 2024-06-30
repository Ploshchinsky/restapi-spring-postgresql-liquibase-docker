package ploton.restapi_postgres_docker.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ploton.restapi_postgres_docker.model.Wallet;

import java.util.UUID;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, UUID> {
}