package sideproject.gugumo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideproject.gugumo.aop.annotation.NeedNotify;
import sideproject.gugumo.domain.dto.CmntDto;
import sideproject.gugumo.domain.dto.memberDto.CustomUserDetails;
import sideproject.gugumo.domain.entity.Cmnt;
import sideproject.gugumo.domain.entity.member.Member;
import sideproject.gugumo.domain.entity.member.MemberStatus;
import sideproject.gugumo.domain.entity.post.Post;
import sideproject.gugumo.exception.exception.CommentNotFoundException;
import sideproject.gugumo.exception.exception.NoAuthorizationException;
import sideproject.gugumo.exception.exception.PostNotFoundException;
import sideproject.gugumo.repository.CmntRepository;
import sideproject.gugumo.repository.MemberRepository;
import sideproject.gugumo.repository.PostRepository;
import sideproject.gugumo.request.CreateCmntReq;
import sideproject.gugumo.request.UpdateCmntReq;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CmntService {

    private final CmntRepository cmntRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;



    @Transactional
//    @NeedNotify     //TODO: 가능하면 AOP로 알림을 보내도록 설계하고 싶다
    public void save(CreateCmntReq req, CustomUserDetails principal) {

        Member author = checkMemberValid(principal, "댓글 등록 실패: 비로그인 사용자입니다.",
                "댓글 등록 실패: 권한이 없습니다.");

        Post targetPost = postRepository.findByIdAndIsDeleteFalse(req.getPostId())
                .orElseThrow(() -> new PostNotFoundException("댓글 등록 실패: 존재하지 않는 게시글입니다."));

        //삭제된 댓글의 대댓글도 작성할 수 있어야 함->deleteFalse를 확인하지 않음
        Cmnt parentCmnt = req.getParentCommentId() != null ?
                cmntRepository.findById(req.getParentCommentId())
                        .orElseThrow(()-> new CommentNotFoundException("대댓글의 상위 댓글이 존재하지 않습니다.")) : null;

        Cmnt cmnt = Cmnt.builder()
                .post(targetPost)
                .parentCmnt(parentCmnt)
                .member(author)
                .content(req.getContent())
                .build();


        cmntRepository.save(cmnt);
        targetPost.increaseCommentCnt();


        //댓글 작성자가 게시글 작성자 본인이 아닐 경우 게시글 작성자에게 알림 전송
        if (!targetPost.getMember().equals(author)) {
            notificationService.send(targetPost.getMember(), cmnt.getContent(),
                    "게시글에 새 댓글이 작성되었습니다", targetPost.getId(), author.getNickname());
        }

    }

    public List<CmntDto> findComment(Long postId, CustomUserDetails principal, Pageable pageable) {

        return cmntRepository.findComment(postId, principal, pageable);


    }

    @Transactional
    public void updateComment(Long commentId, UpdateCmntReq req, CustomUserDetails principal) {

        //member를 먼저 찾아야 equals가 동작하는 이유?
        Member member = checkMemberValid(principal, "댓글 갱신 실패: 비로그인 사용자입니다.",
                "댓글 갱신 실패: 권한이 없습니다.");

        Cmnt cmnt = cmntRepository.findByIdAndIsDeleteFalse(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글 갱신 실패: 해당 댓글이 존재하지 않습니다."));


        //댓글 작성자와 토큰 유저 정보가 다를 경우 처리
        if (!cmnt.getMember().equals(member)) {
            throw new NoAuthorizationException("댓글 갱신 실패: 권한이 없습니다.");
        }

        cmnt.update(req);


    }

    @Transactional
    public void deleteComment(Long commentId, CustomUserDetails principal) {
        //토큰에서
        Member member = checkMemberValid(principal, "댓글 삭제 실패: 비로그인 사용자입니다.",
                "댓글 삭제 실패: 권한이 없습니다.");

        Cmnt cmnt = cmntRepository.findByIdAndIsDeleteFalse(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글 삭제 실패: 존재하지 않는 댓글입니다."));

        if (!cmnt.getMember().equals(member)) {
            throw new NoAuthorizationException("댓글 삭제 실패: 권한이 없습니다.");
        }


        cmnt.tempDelete();
        cmnt.getPost().decreaseCommentCnt();

    }

    private Member checkMemberValid(CustomUserDetails principal, String noLoginMessage, String notValidUserMessage) {
        if (principal == null) {
            throw new NoAuthorizationException(noLoginMessage);
        }

        Member author = memberRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new NoAuthorizationException(notValidUserMessage));

        if (author.getStatus() != MemberStatus.active) {
            throw new NoAuthorizationException(notValidUserMessage);
        }
        return author;
    }


}
