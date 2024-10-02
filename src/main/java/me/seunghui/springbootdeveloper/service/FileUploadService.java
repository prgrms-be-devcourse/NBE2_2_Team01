package me.seunghui.springbootdeveloper.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.FileRepository;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.InsertedFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Log4j2
public class FileUploadService {
    private final FileRepository fileRepository;

    // 여러 파일을 업로드하고 데이터베이스에 InsertedFile 엔티티로 저장하는 메서드
//    public List<InsertedFile> uploadFiles(List<MultipartFile> files, Article article) {
//        List<InsertedFile> uploadedFiles = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            try {
//                // 파일의 바이너리 데이터를 읽음
//                byte[] fileData = file.getBytes();
//
//                // InsertedFile 엔티티 객체를 생성
//                InsertedFile insertedFile = InsertedFile.builder()
//                        .fileName(file.getOriginalFilename()) // 파일명 저장
//                        .fileType(file.getContentType())      // 파일 타입 저장
//                        .fileData(fileData)                   // 파일 데이터를 BLOB으로 저장
//                        .article(article)                     // 해당 파일을 연결할 Article 설정
//                        .build();
//
//                // InsertedFile을 데이터베이스에 저장
//                insertedFile = fileRepository.save(insertedFile);
//
//                uploadedFiles.add(insertedFile);  // 저장된 파일 객체를 리스트에 추가->한번에 저장하는 코드 생각하기
//                log.info("File uploaded: " + insertedFile.getFileName()); // 로그 출력
//            } catch (IOException e) {
//                throw new RuntimeException("File upload failed: " + file.getOriginalFilename(), e);
//            }
//        }
//
//        return uploadedFiles; // 업로드된 파일 리스트 반환
//    }
    public List<InsertedFile>  uploadFiles(List<MultipartFile> files, Article article) {
        List<InsertedFile> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // UUID 파일 이름 생성
                String uuidFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

                // 파일 이진 데이터 가져오기
                byte[] fileData = file.getBytes();

                // InsertedFile 객체 생성
                InsertedFile insertedFile = InsertedFile.builder()
                        .uuidFileName(uuidFileName)
                        .originalFileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileData(fileData)
                        .article(article)
                        .build();

                // InsertedFile을 DB에 저장
                insertedFile = fileRepository.save(insertedFile);

                uploadedFiles.add(insertedFile);
            } catch (IOException e) {
                throw new RuntimeException("File upload failed: " + file.getOriginalFilename(), e);
            }
        }

        return uploadedFiles;
    }

    // 파일 이름을 사용하여 데이터베이스에서 파일을 조회하는 메서드
//    public InsertedFile getFileByFileName(String originalFileName) {
//        return fileRepository.findByFileName(originalFileName)
//                .orElseThrow(() -> new IllegalArgumentException("File not found")); // 파일이 없으면 예외 발생
//    }

    public InsertedFile getFileByArticleIdAndUuidFileName(Long articleId,String  uuidFileName){
        return fileRepository.findByArticleIdAndUuidFileName(articleId,uuidFileName)
                .orElseThrow(() -> new IllegalArgumentException("File not found")); // 파일이 없으면 예외 발생
    }

//    // 임시 디렉터리에 파일을 저장하는 메서드
//    public String saveToTemp(MultipartFile file) throws IOException {
//        // 파일명을 UUID로 변환하여 임시 파일명을 생성
//        String tempFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//        Path tempPath = Paths.get("/temp/uploads/" + tempFileName);  // 저장 경로 설정
//        Files.copy(file.getInputStream(), tempPath);  // 파일을 임시 경로로 복사
//        return tempFileName;  // 임시 파일명 반환
//    }
//
//    // 임시 파일을 데이터베이스로 옮기는 메서드
//    public List<InsertedFile> moveFromTempToDb(List<String> tempFileNames, Article article) throws IOException {
//        List<InsertedFile> savedFiles = new ArrayList<>();
//
//        for (String tempFileName : tempFileNames) {
//            Path tempPath = Paths.get("/temp/uploads/" + tempFileName); // 임시 파일 경로 설정
//            byte[] fileData = Files.readAllBytes(tempPath);  // 임시 파일의 데이터를 읽음
//
//            // InsertedFile 엔티티를 생성하여 파일 데이터를 DB에 저장
//            InsertedFile insertedFile = InsertedFile.builder()
//                    .fileName(tempFileName)
//                    .fileData(fileData)
//                    .article(article)
//                    .build();
//
//            savedFiles.add(fileRepository.save(insertedFile)); // DB에 저장
//            Files.delete(tempPath);  // 임시 파일 삭제
//        }
//
//        return savedFiles; // 저장된 파일 리스트 반환
//    }
//
//    // 임시 파일을 삭제하는 메서드
//    public boolean deleteTempFile(String tempFileName) {
//        try {
//            Path tempPath = Paths.get("/temp/uploads/" + tempFileName); // 임시 파일 경로
//            return Files.deleteIfExists(tempPath);  // 파일이 존재하면 삭제
//        } catch (IOException e) {
//            log.error("Failed to delete temp file: " + tempFileName, e);  // 예외 발생 시 로그 출력
//            return false;
//        }
//    }

}