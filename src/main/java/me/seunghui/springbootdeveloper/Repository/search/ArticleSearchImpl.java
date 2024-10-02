package me.seunghui.springbootdeveloper.Repository.search;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.QArticle;
import me.seunghui.springbootdeveloper.dto.ArticleListViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

// ArticleSearch 인터페이스를 구현하는 클래스
public class ArticleSearchImpl extends QuerydslRepositorySupport implements ArticleSearch {

    // 생성자에서 부모 클래스의 생성자 호출 (Article 클래스에 대한 Querydsl 지원)
    public ArticleSearchImpl() {
        super(Article.class);
    }

    // Page<ArticleListViewResponse>를 반환하는 searchDTO 메서드
    @Override
    public Page<ArticleListViewResponse> searchDTO(Pageable pageable) {
        // QArticle 객체 생성 (Querydsl에서 엔티티를 사용하기 위한 객체)
        QArticle article = QArticle.article;

        // JPQLQuery 생성 (Article 엔티티를 대상으로 함)
        JPQLQuery<Article> query = from(article); // FROM article

        // WHERE 조건 추가: article.id > 0
        query.where(article.id.gt(0L)); // WHERE id > 0

        // 페이징 적용 (pageable 객체를 통해 쿼리에 페이징 설정)
        getQuerydsl().applyPagination(pageable, query);

        // Article 엔티티를 ArticleListViewResponse DTO로 변환 (필요한 필드만 선택)
        JPQLQuery<ArticleListViewResponse> articleQuery = query.select(Projections.constructor(
                ArticleListViewResponse.class,
                article // 엔티티를 DTO로 매핑
        ));

        // 쿼리 실행 후 결과를 리스트로 가져옴
        List<ArticleListViewResponse> articleListPage = articleQuery.fetch(); // 쿼리 실행

        // 전체 레코드 수를 조회 (페이지네이션에 필요한 총 레코드 수)
        long count = query.fetchCount(); // 레코드 수 조회

        // 조회된 결과를 PageImpl 객체로 반환 (페이지네이션을 위한 처리)
        return new PageImpl<>(articleListPage, pageable, count);
    }
}
