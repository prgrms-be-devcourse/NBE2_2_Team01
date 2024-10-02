package me.seunghui.springbootdeveloper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component  // Spring의 빈(Bean)으로 등록하여 다른 컴포넌트에서 자동으로 주입 가능하게 함
//파일 업로드 같은 Multipart 요청 또는 바이너리 데이터가 포함된 요청(octet-stream)도 처리할 수 있도록 지원
public class MultipartJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    // 생성자: ObjectMapper를 인자로 받아 부모 클래스인 MappingJackson2HttpMessageConverter의 생성자를 호출
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);  // 부모 클래스에 ObjectMapper를 전달

        // 지원하는 미디어 타입을 설정. 이 변환기는 APPLICATION_OCTET_STREAM 및 MULTIPART_FORM_DATA 요청을 처리할 수 있음
        this.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_OCTET_STREAM,  // 이 변환기가 application/octet-stream 요청을 처리할 수 있게 함
                MediaType.MULTIPART_FORM_DATA  // 이 변환기가 multipart/form-data 요청을 처리할 수 있게 함
        ));
    }
}

//Multipart 데이터 처리 문제: 글을 작성하거나 수정할 때, CKEditor에서 이미지를 업로드할 때 Multipart 데이터가 제대로 처리되지 않아서 415 Unsupported Media Type 오류가 발생
// MultipartJackson2HttpMessageConverter를 커스터마이징하여 multipart/form-data를 처리할 수 있도록 수정