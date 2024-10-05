package me.seunghui.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value={AuditingEntityListener.class}) //@EntityListeners(AuditingEntityListener.class)가 엔티티의 생성 및 수정 시점에 자동으로 시간을 기록할 수 있다.
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",nullable = false)
    private Long id;

    @Column(name="title",nullable = false)
    private String title;

    @Column(name="content",nullable = false)
    private String content;

    // "InsertedFile" 엔티티와의 일대다 관계 설정
    // 'mappedBy'는 'InsertedFile'의 'article' 필드를 참조함
    // 'cascade = CascadeType.ALL'은 관련 파일들이 article의 생명 주기에 따라 동작함
    // 'orphanRemoval = true'는 연관된 파일이 제거되면 고아 객체로 남지 않고 삭제됨
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // 변경된 부분
    @BatchSize(size = 100)
    private List<InsertedFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // 댓글 리스트

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); // 댓글 리스트

    @Column(name = "author",nullable = false)
    private String author;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name="view_count")
    private Long viewCount= 0L;

    @Column(name="like_count")
    private Long likeCount= 0L;

    // 파일을 추가하는 메서드, 양방향 연관 관계 설정
    public void addFiles(List<InsertedFile> files) {
        if (files != null) {
            for (InsertedFile file : files) {
                file.changeArticle(this); // 각 파일 객체에 article 객체를 설정
                this.files.add(file); // 현재 article 객체에 파일을 추가
            }
        }
    }

    public Article(String author, String title, String content){
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }



    public void changeAuthor(String author) {this.author = author;}
    public void changeTitle(String title) {this.title = title;}
    public void changeContent(String content) {this.content = content;}
    public void isIncrementViewCount() {this.viewCount++;}
    public void changeLikeCount(long likeCount) {this.likeCount=likeCount;}
}
