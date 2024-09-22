package me.seunghui.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.seunghui.springbootdeveloper.Repository.BlogRepository;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.dto.AddArticleRequest;
import me.seunghui.springbootdeveloper.dto.UpdateArticleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {
    // HTTP 요청을 시뮬레이션하기 위한 클래스
    // 실제 웹 서버를 실행하지 않고도 컨트롤러의 기능을 테스트할 수 있게 해줍니다.
    @Autowired
    private MockMvc mockMvc;

    // 객체를 JSON으로 직렬화하거나, JSON을 객체로 역직렬화하는 데 사용
    // 테스트에서 요청 본문을 JSON 형식으로 변환할 때 사용
    @Autowired
    protected ObjectMapper objectMapper; //직렬화, 역질혁화를 위한 클래스

    //Spring 애플리케이션의 웹 애플리케이션 컨텍스트를 나타냄
    // 테스트 중에 MockMvc를 설정하는 데 사용됨
    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;
    
    @Autowired
    UserRepository userRepository;
    
    User user;

    // MockMvcBuilders.webAppContextSetup(): Spring의 실제 WebApplicationContext를 기반으로 MockMvc를 설정하는 방법
    // WebApplicationContext는 Spring에서 웹 애플리케이션 관련 빈(bean)을 관리하는 스프링 컨테이너
    //이는 Spring MVC 애플리케이션에서 DispatcherServlet과 같은 웹 관련 구성 요소를 포함하며, 컨트롤러, 뷰 리졸버, 요청 매핑 등의 설정을 관리한다.
    @BeforeEach
    public void mockMvcSetup(){
        this.mockMvc= MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }
    
    @BeforeEach
    void setSecurityContext(){
        userRepository.deleteAll();
        user=userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build());

        SecurityContext context= SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }


    @DisplayName("addArticle: 아티클 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {

        final String url="/api/articles";
        final String title="title";
        final String content="content";
        //아티클 글의 제목과 내용을 담은 요청 객체를 생성
        final AddArticleRequest userRequest=new AddArticleRequest(title,content);

        //요청 객체를 JSON 문자열로 직렬화
        final String requestBody=objectMapper.writeValueAsString(userRequest); //객체 JSON으로 직렬화
        //역직렬화: JSON 문자열을 자바 객체로 변환
        // JSON 문자열 예시
        //String jsonString = "{\"title\":\"title\",\"content\":\"content\"}";
        //AddArticleRequest addArticleRequest = objectMapper.readValue(jsonString, AddArticleRequest.class);
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        //설정한 내용을 바탕으로 /api/articles URL에 HTTP POST 요청을 보냅니다. 요청 본문은 JSON 형식입니다.
        //설정한 내용을 바탕으로 요청 전송
        ResultActions result=mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).principal(principal).content(requestBody));

        result.andExpect(status().isCreated());

        //데이터베이스에서 모든 아티클 글을 조회
        List<Article> articles=blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);

    }

    @DisplayName("findAllArticles: 아티클 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {

        final String url="/api/articles";
        Article savedArticle = createDefaultArticle();

        //설정한 내용을 바탕으로 /api/articles URL에 HTTP GET 요청을 보냅니다.
        final ResultActions resultActions=mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

    @DisplayName("findArticle: 아티클 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        final String url="/api/articles/{id}";
        final String title="title";
        Article savedArticle=createDefaultArticle();
        
        final ResultActions resultActions=mockMvc.perform(get(url,savedArticle.getId()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
    }

    @DisplayName("deleteArticle: 아티클 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        final String url="/api/articles/{id}";
        Article savedArticle=createDefaultArticle();


        mockMvc.perform(delete(url,savedArticle.getId()))
                .andExpect(status().isOk());

        List<Article> articles=blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 아티클 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        final String url="/api/articles/{id}";
        Article savedArticle=createDefaultArticle();

        final String newTitle="newTitle";
        final String newContent="newContent";

        UpdateArticleRequest request=new UpdateArticleRequest(newTitle,newContent);

        ResultActions result=mockMvc.perform(put(url,savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        Article article=blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);

    }

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }
}