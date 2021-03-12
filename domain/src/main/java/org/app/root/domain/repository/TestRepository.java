package org.app.root.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.app.root.domain.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID>{

}
