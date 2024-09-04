package com.project.react_tft.domain;

import com.project.react_tft.dto.MemberDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table
public class Member extends BaseEntity {

    @OneToMany
    @ToString.Exclude
    private List<Review> reviews;

    @Id
    private String mid;

    private String mname;
    private String mnick;
    private String mpw;
    private String memail;
    private String mphone;
    private boolean del;
    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<MemberRole> roleSet = new HashSet<>();

    public Member(MemberDTO dto) {
        this.mid = dto.getMid();
        this.mpw = dto.getMpw();
        this.mname = dto.getMname();
        this.mnick = dto.getMnick();
        this.memail = dto.getMemail();
        this.mphone = dto.getMphone();
    }

    //addRoll 역할 추가
    public void addRole(MemberRole role) {
        this.roleSet.add(role);
    }

    //삭제 여부(유저가 계정탈퇴를 원할시 바로삭제하지 않게)
    public void changeDel(boolean del) {
        this.del = del;
    }

}
