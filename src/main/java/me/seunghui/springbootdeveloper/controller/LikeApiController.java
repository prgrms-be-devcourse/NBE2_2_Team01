package me.seunghui.springbootdeveloper.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.domain.Like;
import me.seunghui.springbootdeveloper.dto.Like.LikeRequest;
import me.seunghui.springbootdeveloper.service.LikeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/like")
public class LikeApiController {
    private final LikeService likeService;

//    @PostMapping("/{articleId}")
//    public ResponseEntity<Like> addLike(@PathVariable Long articleId, Principal principal) {
//        Like savedLike = likeService.saveLike(articleId, principal.getName());
//        return ResponseEntity.ok(savedLike);
//    }
//
//    @PutMapping("/{likeId}")
//    public ResponseEntity<Like> updateLike(@PathVariable Long likeId, Principal principal) {
//        Like updatedLike = likeService.updateLike(likeId);
//        return ResponseEntity.ok(updatedLike);
//    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody LikeRequest request, Principal principal) {
        // `addLike` 메서드가 boolean 값을 반환하고, 그것을 그대로 사용
        boolean likedStatus = likeService.addLike(request.getArticleId(), principal.getName());

        // 좋아요 수 가져오기
        long likeCount = likeService.getLikeCount(request.getArticleId());

        log.info("ArticleId:{} " , request.getArticleId());
        Map<String, Object> response = new HashMap<>();
        response.put("likedStatus", likedStatus); // 반환할 likedStatus 추가
        response.put("likeCount", likeCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(@RequestParam Long articleId, Principal principal) {
        // 현재 사용자의 좋아요 상태를 확인
        boolean likedStatus = likeService.checkLikeStatus(articleId, principal.getName());

        // 응답으로 likedStatus를 반환
        Map<String, Boolean> response = new HashMap<>();
        response.put("likedStatus", likedStatus);
        return ResponseEntity.ok(response);
    }


}
