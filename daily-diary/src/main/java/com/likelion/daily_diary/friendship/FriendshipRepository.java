package com.likelion.daily_diary.friendship;

import com.likelion.daily_diary.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :a AND f.receiver = :b) OR (f.requester = :b AND f.receiver = :a)")
    Optional<Friendship> findBetween(@Param("a") Member a, @Param("b") Member b);

    List<Friendship> findByReceiverAndStatus(Member receiver, FriendshipStatus status);

    List<Friendship> findByRequesterAndStatus(Member requester, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :member OR f.receiver = :member) AND f.status = :status")
    List<Friendship> findByMemberAndStatus(@Param("member") Member member, @Param("status") FriendshipStatus status);
}
