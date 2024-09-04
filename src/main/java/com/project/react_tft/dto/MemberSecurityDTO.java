package com.project.react_tft.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User implements OAuth2User {

    private String mid;

    private String mpw;

    private String mnick;

    private String memail;

    private boolean del;

    private boolean social;

    private String token;

    private Map<String, Object> props; //소셜 로그인 정보

    public MemberSecurityDTO(String username, String password,String mnick ,String memail, boolean del, boolean social,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);

        this.mid = username;
        this.mpw = password;
        this.mnick = mnick;
        this.memail = memail;
        this.del = del;
        this.social = social;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.mid;
    }
}
