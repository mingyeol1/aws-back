package com.project.react_tft.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY) //다대일 관계로 구성됨... (연관관계시 fetch = FetchType.LAZY로 구성)
    private Board board;               // board를 구분할 수 있는 PK인 bno가 들어감...

    private String replyText;

    private String replyer;

    public void changeText(String text) {
        this.replyText = text;
    }

    // board 값 설정을 위해서 -> bno를 받아서 생성...
    public void setBoard(Long bno) {
        this.board = Board.builder().bno(bno).build();
    }

}
