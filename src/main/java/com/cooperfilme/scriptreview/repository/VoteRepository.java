package com.cooperfilme.scriptreview.repository;

import com.cooperfilme.scriptreview.entity.Vote;
import com.cooperfilme.scriptreview.entity.Script;
import com.cooperfilme.scriptreview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByScript(Script script);
    boolean existsByScriptAndApprover(Script script, User approver);
}
