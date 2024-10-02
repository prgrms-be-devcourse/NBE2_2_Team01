package me.seunghui.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "inserted_file")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class InsertedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키 추가

    @Column(nullable = false)
    private String uuidFileName; // UUID 파일 이름

    @Column(nullable = false)
    private String originalFileName; // 원래 파일 이름
    //private String fileName;
    private String fileType; // 파일 타입(MIME 타입)을 저장하는 필드

    @Lob // 큰 데이터 블록을 저장할 수 있는 Lob 타입으로 지정
    @Column(name = "file_data", nullable = false, columnDefinition = "LONGBLOB")
    // LONGBLOB은 매우 큰 파일 데이터를 저장할 수 있는 타입으로 설정
    private byte[] fileData; // 파일의 실제 데이터를 바이트 배열로 저장

    @ManyToOne // 여러 개의 InsertedFile이 하나의 Article에 속함
    @JoinColumn(name = "article_id", nullable = false) // 외래키 설정
    private Article article; // InsertedFile이 속한 Article 객체를 참조


    public void changeArticle(Article article) {
        this.article = article;
    }
    public void changeFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    public void changeFileType(String fileType) {
        this.fileType = fileType;
    }
    public void changeFileData(byte[] fileData) {
        this.fileData = fileData;
    }


}
