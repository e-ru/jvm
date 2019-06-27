package eu.rudisch.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.rudisch.authorizationserver.model.ClientDetail;

public interface ClientDetailsRepository extends JpaRepository<ClientDetail, String> {

}
