package me.seunghui.springbootdeveloper.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.chatting.VideoChatLogRepository;
import me.seunghui.springbootdeveloper.domain.VideoChatLog;
import me.seunghui.springbootdeveloper.dto.chat.VideoChatLogDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class VideoChatLogService {

    private final VideoChatLogRepository videoChatLogRepository;

    public VideoChatLogDTO videoChatStartTimeLog(VideoChatLogDTO videoChatLogDTO){ // 등록
        VideoChatLog videoChatLog = videoChatLogDTO.toEntity();
        videoChatLogRepository.save(videoChatLog);
        return new VideoChatLogDTO(videoChatLog);
    }

    public void videoChatEndTimeLog(String videoChatId) { // 등록

        Optional<VideoChatLog> videoChatStartTimeLog =
                videoChatLogRepository.findById(videoChatId);

        VideoChatLog videoChatLog = videoChatStartTimeLog.orElseThrow(() ->
                new IllegalArgumentException("해당 ID의 로그를 찾을 수 없습니다: " + videoChatId));

        // video_chat_end_at 값 설정
        videoChatLog.setVideo_chat_end_at(LocalDateTime.now());

        // 변경된 엔티티 저장 (JPA가 변경사항을 감지하고 UPDATE 쿼리를 실행함)
        videoChatLogRepository.save(videoChatLog);
        System.out.println("종료 타임 저장됨");
    }
}
