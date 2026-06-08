package com.likelion.daily_diary.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.id != :excludeId AND m.deletedAt IS NULL " +
            "AND (LOWER(m.nickname) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(m.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Member> searchByNicknameOrEmail(@Param("q") String q, @Param("excludeId") Long excludeId);
}
