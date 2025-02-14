package com.project.react_tft.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false)     // 칼럼의 길이와 null허용 여부
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_mid")
    private Member member;

    @OneToMany(mappedBy = "board"
            ,orphanRemoval = true)
    private Set<Reply> reply = new HashSet<>();


    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }
}
