package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//@AllArgsConstructor // 모든 필드 생성자 만들어서 초기화
public class Logininfo {
    private int userId;
    private String email;
    private String  name;
    private List<String> roles = new ArrayList<>();

    private void addRole(String roleName) {
        roles.add(roleName);
    }

    public Logininfo(int userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }
}
