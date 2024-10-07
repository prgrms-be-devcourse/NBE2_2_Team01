package me.seunghui.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.domain.Comment;
import me.seunghui.springbootdeveloper.dto.Comment.AddCommentRequest;
import me.seunghui.springbootdeveloper.dto.Comment.CommentResponse;
import me.seunghui.springbootdeveloper.dto.Comment.UpdateCommentRequest;
import me.seunghui.springbootdeveloper.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/comment")
public class CommentApiController {
    private final CommentService commentService;
    //1. 게시글에 맞는 한개 댓글 생성
    @PostMapping("/{articleId}")
    public ResponseEntity<Comment> addComment(@PathVariable("articleId") Long articleId,
                                              @RequestBody AddCommentRequest request
                                                , Principal principal) {
        Comment savedComment=commentService.saveComment(request,articleId,principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

//    @PostMapping("/{articleId}")
//    public ResponseEntity<?> addComment(@PathVariable("articleId") Long articleId,
//                                              @RequestBody AddCommentRequest request
//            , Principal principal) {
//        commentService.saveComment(request,articleId,principal.getName());
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Comment added successfully");
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    //2. 게시글에 달린 댓글 목록 조회 (시간순)
    @GetMapping(value = "/{articleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentResponse>> findComments(@PathVariable("articleId") Long articleId) {
        List<CommentResponse> comments=commentService.getComments(articleId);
        // 각 댓글 내용 로그 출력
//        for (CommentResponse comment : comments) {
//            log.info("Comment Author: {}, Content: {}, CreatedAt: {}",
//                    comment.getCommentAuthor(), comment.getCommentContent(), comment.getCommentCreatedAt());
//        }
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    //3. 게시글에 맞는 한개 댓글과 대댓글 조회
    @GetMapping(value = "/{articleId}/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentResponse>> findReComments(@PathVariable("articleId") Long articleId,@PathVariable("commentId") Long commentId) {
        List<CommentResponse> comments=commentService.getReComments(articleId,commentId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }
    //4. 댓글 수정
    //4-1. '삭제된 댓글입니다'
    //4-2. '관리자에 의해 블라인드 처리된 댓글입니다.'
//    @PutMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<CommentResponse> updateComment(@PathVariable("commentId") Long commentId,
//                                              @RequestBody UpdateCommentRequest request) {
//        CommentResponse updatedComment=commentService.updateComment(commentId,request);
//        return ResponseEntity.status(HttpStatus.OK).body( updatedComment);
//    }
    @PutMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateCommentRequest> updateComment(@PathVariable("commentId") Long commentId,
                                              @RequestBody UpdateCommentRequest request) {

//        // 로그 추가
       log.info("API Request: commentId = {}, isDeleted = {}, content = {}", commentId, request.isCommentIsDeleted(), request.getCommentContent());

        UpdateCommentRequest updatedComment=commentService.updateComment(commentId,request);


        // 로그 추가: 업데이트된 댓글의 상태 확인
        log.info("Updated Comment: commentId = {}, isDeleted = {}, content = {}", commentId, updatedComment.isCommentIsDeleted(), updatedComment.getCommentContent());

        return ResponseEntity.status(HttpStatus.OK).body( updatedComment);
    }

    //5. 댓글 삭제
    @DeleteMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
