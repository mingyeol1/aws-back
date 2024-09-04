package com.project.react_tft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name="Review",
        uniqueConstraints = {
                @UniqueConstraint(
                        name="review_id_unique",
                        columnNames="review_id"
                )})
public class Review extends BaseEntity {

    //FK설정 (관계)
    @ManyToOne(fetch = FetchType.LAZY)
    //컬럼 이름설정
    @JoinColumn(name = "mid")
    //toString에서 제외
    //@ToString.Exclude
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    //리뷰id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 200, nullable = false)
    private Long review_id;

    //리뷰내용
    @Column(length = 10000)
    private String review_text;

    //별점
    @Column(length = 200)
    @Builder.Default
    private Integer review_star = 0;

    // 엔티티 내에서 변경 가능한 title과 content 값을 수정하는 메서드.
    public void change(String review_text, Integer review_star){
        this.review_text = review_text;
        this.review_star = review_star;
    }

}
