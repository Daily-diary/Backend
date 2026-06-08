package com.likelion.daily_diary.friendship;

import com.likelion.daily_diary.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"requester_id", "receiver_id"}))
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FriendshipStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Friendship() {
    }

    public Friendship(Member requester, Member receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = FriendshipStatus.PENDING;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
    }

    public Member getOtherMember(Long myId) {
        return requester.getId().equals(myId) ? receiver : requester;
    }

    public Long getId() {
        return id;
    }

    public Member getRequester() {
        return requester;
    }

    public Member getReceiver() {
        return receiver;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
