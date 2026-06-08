package com.likelion.daily_diary.friendship;

import com.likelion.daily_diary.friendship.dto.FriendRequestResponse;
import com.likelion.daily_diary.friendship.dto.FriendResponse;
import com.likelion.daily_diary.member.Member;
import com.likelion.daily_diary.member.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final MemberRepository memberRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, MemberRepository memberRepository) {
        this.friendshipRepository = friendshipRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void sendRequest(Member me, Long receiverId) {
        if (me.getId().equals(receiverId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }
        Member receiver = findActiveMember(receiverId);

        friendshipRepository.findBetween(me, receiver).ifPresent(f -> {
            throw switch (f.getStatus()) {
                case PENDING -> f.getRequester().getId().equals(me.getId())
                        ? new IllegalStateException("이미 친구 요청을 보냈습니다.")
                        : new IllegalStateException("상대방이 이미 친구 요청을 보냈습니다.");
                case ACCEPTED -> new IllegalStateException("이미 친구입니다.");
            };
        });

        friendshipRepository.save(new Friendship(me, receiver));
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(Member me) {
        return friendshipRepository.findByMemberAndStatus(me, FriendshipStatus.ACCEPTED).stream()
                .map(f -> FriendResponse.from(f, me.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getReceivedRequests(Member me) {
        return friendshipRepository.findByReceiverAndStatus(me, FriendshipStatus.PENDING).stream()
                .map(FriendRequestResponse::fromReceived)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getSentRequests(Member me) {
        return friendshipRepository.findByRequesterAndStatus(me, FriendshipStatus.PENDING).stream()
                .map(FriendRequestResponse::fromSent)
                .toList();
    }

    @Transactional
    public void acceptRequest(Member me, Long friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        if (!friendship.getReceiver().getId().equals(me.getId())) {
            throw new IllegalArgumentException("수락 권한이 없습니다.");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        friendship.accept();
    }

    @Transactional
    public void rejectRequest(Member me, Long friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        if (!friendship.getReceiver().getId().equals(me.getId())) {
            throw new IllegalArgumentException("거절 권한이 없습니다.");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        friendshipRepository.delete(friendship);
    }

    @Transactional
    public void cancelRequest(Member me, Long friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        if (!friendship.getRequester().getId().equals(me.getId())) {
            throw new IllegalArgumentException("취소 권한이 없습니다.");
        }
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        friendshipRepository.delete(friendship);
    }

    @Transactional
    public void removeFriend(Member me, Long friendMemberId) {
        Member friend = findActiveMember(friendMemberId);
        Friendship friendship = friendshipRepository.findBetween(me, friend)
                .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));
        friendshipRepository.delete(friendship);
    }

    private Friendship getFriendshipOrThrow(Long friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
    }

    private Member findActiveMember(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
}
