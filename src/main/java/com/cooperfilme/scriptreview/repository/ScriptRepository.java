package com.cooperfilme.scriptreview.repository;

import com.cooperfilme.scriptreview.entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScriptRepository extends JpaRepository<Script, Long> {

}