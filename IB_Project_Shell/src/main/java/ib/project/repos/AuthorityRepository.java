package ib.project.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ib.project.model.Authority;


public interface AuthorityRepository extends JpaRepository<Authority, Long>{
	Optional<Authority> findByName(String name);
}
