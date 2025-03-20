package com.filips.healthServer.repository;

import com.filips.healthServer.model.Mod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandRepo extends JpaRepository<Mod, Integer> {
}
