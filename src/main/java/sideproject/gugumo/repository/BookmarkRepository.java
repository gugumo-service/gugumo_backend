package sideproject.gugumo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sideproject.gugumo.domain.Bookmark;
import sideproject.gugumo.domain.Member;
import sideproject.gugumo.domain.post.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    public Page<Bookmark> findByMember(Member member, Pageable pageable);

    public Optional<Bookmark> findByMemberAndPost(Member member, Post post);

    public boolean existsByMemberAndPost(Member member, Post post);

    public Long countByPost(Post post);

    public void deleteByPost(Post post);


}