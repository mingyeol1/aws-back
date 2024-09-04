package com.project.react_tft.Repository;

import com.project.react_tft.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    public boolean existsByMidAndMpw(String mid, String mpw);

    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.mid = :mid and m.del = false and m.social = false ")
    Optional<Member> getWithRoles(String mid);

    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findByMemail(String memail);


    boolean existsByMemail(String memail);
    boolean existsByMnick(String mnick);
}
