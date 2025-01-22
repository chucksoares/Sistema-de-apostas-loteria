package com.example.loteriasucess.repositories;

import com.example.loteriasucess.domain.Lotofacil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LotofacilRepository extends JpaRepository<Lotofacil, Long> {
    List<Lotofacil> findTop10ByOrderByConcursoDesc();
}
