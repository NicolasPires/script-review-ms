package com.cooperfilme.scriptreview.repository;

import com.cooperfilme.scriptreview.entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findByClientEmail(String email);
}