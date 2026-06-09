package com.likelion.daily_diary.domain.user.repository;

import com.likelion.daily_diary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByFirebaseUid(String firebaseUid);

    @Query("SELECT u FROM User u WHERE u.id != :excludeId " +
            "AND (LOWER(u.nickname) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<User> searchByNicknameOrEmail(@Param("q") String q, @Param("excludeId") UUID excludeId);
}
