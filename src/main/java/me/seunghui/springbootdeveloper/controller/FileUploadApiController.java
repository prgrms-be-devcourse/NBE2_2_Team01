package me.seunghui.springbootdeveloper.controller;


import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.InsertedFile;
import me.seunghui.springbootdeveloper.service.ArticleService;
import me.seunghui.springbootdeveloper.service.FileUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
@Log4j2 // 로깅을 위한 어노테이션
public class FileUploadApiController {
    private final ArticleService articleService;
    private final FileUploadService fileUploadService;

    // 파일 업로드 API (CKEditor에서 사용)
    //CKEditor에서 업로드된 파일을 받아, 해당 파일을 데이터베이스에 BLOB으로 저장한 후 파일 URL을 반환
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestPart("upload") MultipartFile file, @RequestParam Long articleId) throws IOException {
        // articleId로 Article 객체 조회
        Article article = articleService.findById(articleId);

        // 파일을 업로드하고 InsertedFile 객체 생성 (파일을 BLOB으로 저장)
        List<InsertedFile> uploadedFiles = fileUploadService.uploadFiles(List.of(file), article);

        // 업로드된 파일의 URL 생성 및 반환
      //  String fileUrl = "/api/upload/file/" + URLEncoder.encode(uploadedFiles.get(0).getFileName(), StandardCharsets.UTF_8.toString());
        // 업로드된 파일의 URL 생성 및 반환 (uuidFileName 사용)
        String fileUrl = "/api/upload/file?articleId=" + articleId + "&uuidFileName=" + URLEncoder.encode(uploadedFiles.get(0).getUuidFileName(), StandardCharsets.UTF_8.toString());

        //log.info("fileUrl: " + fileUrl);

        // CKEditor에서 요구하는 JSON 응답 형식으로 업로드된 파일의 URL 반환
        Map<String, Object> response = new HashMap<>();
        response.put("uploaded", true);
        response.put("url", fileUrl);

        return ResponseEntity.ok(response);
    }

    // 파일 다운로드 API (BLOB 데이터를 반환)
//    @GetMapping("/file/{fileName}")
//    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
//        try {
//            // 파일 이름을 URL 디코딩
//            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
//
//            // 파일 이름으로 InsertedFile 조회
//            InsertedFile insertedFile = fileUploadService.getFileByFileName(decodedFileName);
//            // articleId와 uuidFileName으로 InsertedFile 조회
//
//            if (insertedFile == null) {
//                return ResponseEntity.notFound().build(); // 파일이 없으면 404 응답
//            }
//
//            byte[] fileData = insertedFile.getFileData(); // 파일 데이터 (BLOB)
//
//            // MIME 타입 설정
//            MediaType mediaType = MediaType.parseMediaType(insertedFile.getFileType());
//
//            // 파일명을 UTF-8로 인코딩
//            String encodedFileName = URLEncoder.encode(insertedFile.getFileName(), StandardCharsets.UTF_8.toString());
//
//            // 파일 데이터와 함께 응답 생성
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFileName) // 다운로드 시 파일명
//                    .contentType(mediaType) // 파일의 MIME 타입
//                    .body(fileData); // 파일 데이터 전송
//        } catch (IOException e) {
//            log.error("Error processing file download", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 오류 발생 시 500 응답
//        }
//    }
//    @GetMapping("/file/{articleId}/{uuidFileName}")
    @GetMapping("/file")
    public ResponseEntity<byte[]> getFile(@RequestParam Long articleId, @RequestParam String uuidFileName) {
        try {
            // articleId와 uuidFileName으로 InsertedFile 조회
            InsertedFile insertedFile = fileUploadService.getFileByArticleIdAndUuidFileName(articleId, uuidFileName);

            if (insertedFile == null) {
                return ResponseEntity.notFound().build(); // 파일이 없으면 404 응답
            }

            byte[] fileData = insertedFile.getFileData(); // 파일 데이터

            // MIME 타입 설정
            MediaType mediaType = MediaType.parseMediaType(insertedFile.getFileType());

            // 파일명 인코딩
            String encodedFileName = URLEncoder.encode(insertedFile.getOriginalFileName(), StandardCharsets.UTF_8.toString());

            // 파일 데이터와 함께 응답 생성
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFileName) // 원래 파일 이름으로 다운로드
                    .contentType(mediaType)
                    .body(fileData);
        } catch (IOException e) {
            log.error("Error processing file download", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
//    // 임시 파일 업로드 API (임시 저장소에 저장)
//    @PostMapping("/temp")
//    public ResponseEntity<?> uploadTempFile(@RequestPart("upload") MultipartFile file) throws IOException {
//        // 파일을 임시 저장소에 저장
//        String tempFileName = fileUploadService.saveToTemp(file);
//
//        // 저장된 임시 파일의 URL 반환
//        String fileUrl = "/api/upload/temp-file/" + URLEncoder.encode(tempFileName, StandardCharsets.UTF_8.toString());
//
//        // 업로드 성공 응답
//        Map<String, Object> response = new HashMap<>();
//        response.put("uploaded", true);
//        response.put("url", fileUrl);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // 임시 파일을 실제 데이터베이스로 이동하는 API
//    @PostMapping("/save-files")
//    public ResponseEntity<?> saveTempFiles(@RequestParam Long articleId, @RequestBody List<String> tempFileNames) throws IOException {
//        // articleId로 Article 조회
//        Article article = blogService.findById(articleId);
//
//        // 임시 파일들을 실제 데이터베이스로 이동하여 InsertedFile 객체로 변환
//        List<InsertedFile> savedFiles = fileUploadService.moveFromTempToDb(tempFileNames, article);
//
//        return ResponseEntity.ok("Files saved successfully with article"); // 성공 메시지 응답
//    }
//
//    // 임시 파일 삭제 API
//    @DeleteMapping("/temp")
//    public ResponseEntity<?> deleteTempFile(@RequestParam String tempFileName) {
//        // 임시 파일 삭제
//        boolean deleted = fileUploadService.deleteTempFile(tempFileName);
//
//        if (deleted) {
//            return ResponseEntity.ok("Temporary file deleted"); // 삭제 성공 시 응답
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete temporary file"); // 삭제 실패 시 500 응답
//        }
//    }
}

//디코딩: 클라이언트가 인코딩하여 보내는 URL 파라미터를 원래 파일명으로 변환하기 위해.
//인코딩: 파일명에 특수문자나 공백이 있을 때, 브라우저가 URL로 요청할 수 있도록 안전하게 변환하기 위해.