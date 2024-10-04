package me.seunghui.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.ArticleRepository;
import me.seunghui.springbootdeveloper.Repository.CommentRepository;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.Comment;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.dto.Comment.AddCommentRequest;
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

//import static me.seunghui.springbootdeveloper.domain.QArticle.article;
import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
class CommentApiControllerTest {
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
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    Article article;

    // MockMvcBuilders.webAppContextSetup(): Spring의 실제 WebApplicationContext를 기반으로 MockMvc를 설정하는 방법
    // WebApplicationContext는 Spring에서 웹 애플리케이션 관련 빈(bean)을 관리하는 스프링 컨테이너
    //이는 Spring MVC 애플리케이션에서 DispatcherServlet과 같은 웹 관련 구성 요소를 포함하며, 컨트롤러, 뷰 리졸버, 요청 매핑 등의 설정을 관리한다.
    @BeforeEach
    public void mockMvcSetup(){
        this.mockMvc= MockMvcBuilders.webAppContextSetup(context).build();
        articleRepository.deleteAll();
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
    @BeforeEach
    void setupArticle() {
        article = articleRepository.save(Article.builder()
                .title("Test Article")
                .content("Test Content")
                .author(user.getEmail())
                .build());
    }
    @DisplayName("addComment: 댓글 추가에 성공한다.")
    @Test
    public void addComment() throws Exception {

        final String url="/api/articles/71";
        final String commentContent="content";
        final Long parentCommentId=null;

        final AddCommentRequest userRequest=new AddCommentRequest(commentContent,parentCommentId);

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
        List<Comment> comments= commentRepository.findAll();

        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getCommentContent()).isEqualTo(commentContent);
        assertThat(comments.get(0).getArticle().getId()).isEqualTo(article.getId()); // 댓글이 올바른 article에 연결되었는지 확인

    }

    @DisplayName("getComments: 특정 articleId의 댓글 목록을 조회한다.")
    @Test
    public void getComments() throws Exception {
        // 댓글 사전 저장
        Comment savedComment1 = commentRepository.save(Comment.builder()
                .commentAuthor(user.getEmail())
                .commentContent("First Comment")
                .article(article)
                .build());

        Comment savedComment2 = commentRepository.save(Comment.builder()
                .commentAuthor(user.getEmail())
                .commentContent("Second Comment")
                .article(article)
                .build());

        final String url = "/api/articles/" + article.getId() + "/comments";  // 댓글 조회를 위한 URL

        // 댓글 조회 요청 (GET 요청)
        ResultActions result = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // 상태 코드 확인 및 JSON 응답 검증
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))  // 2개의 댓글이 조회되는지 확인
                .andExpect(jsonPath("$[0].commentContent").value("First Comment"))
                .andExpect(jsonPath("$[1].commentContent").value("Second Comment"));
    }


}