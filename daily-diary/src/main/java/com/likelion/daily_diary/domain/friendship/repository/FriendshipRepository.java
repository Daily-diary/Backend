package com.likelion.daily_diary.domain.friendship.repository;

import com.likelion.daily_diary.domain.friendship.entity.Friendship;
import com.likelion.daily_diary.domain.friendship.entity.FriendshipStatus;
import com.likelion.daily_diary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    @Query("SELECT f FROM Friendship f WHERE f.status = :status AND (f.requester = :user OR f.receiver = :user)")
    List<Friendship> findByStatusAndUser(@Param("status") FriendshipStatus status, @Param("user") User user);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE f.status = 'ACCEPTED' AND " +
            "((f.requester = :user1 AND f.receiver = :user2) OR (f.requester = :user2 AND f.receiver = :user1))")
    boolean areFriends(@Param("user1") User user1, @Param("user2") User user2);
}
