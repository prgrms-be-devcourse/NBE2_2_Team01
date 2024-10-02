//package me.seunghui.springbootdeveloper.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.ByteArrayHttpMessageConverter;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        // application/octet-stream을 지원하는 컨버터 추가
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//
//        // JSON을 처리하는 HttpMessageConverter에 multipart/form-data 미디어 타입 지원 추가
//        List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
//        supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
//        converter.setSupportedMediaTypes(supportedMediaTypes);
//
//        converters.add(converter);
//    }
//
//}
