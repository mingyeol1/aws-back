package com.project.react_tft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "meetBoard")
@Table(name = "MeetReply", indexes = {
        @Index(name = "idx_reply_meet_board_meet_id", columnList = "meet_board_meet_id")
})
public class MeetReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetRid;

    @ManyToOne(fetch = FetchType.LAZY)
    private MeetBoard meetBoard;

    private String replyText;

    private String replyer;

    public void changeText(String text) {
        this.replyText = text;
    }
}
