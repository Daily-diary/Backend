package com.likelion.daily_diary.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);
}
