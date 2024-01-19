package com.example.board.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

//lombok으로 getter, setter 설정
@Setter
@Getter
@NoArgsConstructor // 기본 생성자 자동을 생성
@ToString // Object의 toString() 메서드 자동으로 생성하여 문자열로 반환
public class User {
    private int userId;
    private String email;
    private String name;
    private String password;
    private LocalDateTime regdate; // 원래는 date 타입으로 읽어온 후 문자열로 변환하는 과정 필요


    /*
    'user_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
    'email', 'varchar(255)', 'NO', '', NULL, ''
    'name', 'varchar(50)', 'NO', '', NULL, ''
    'password', 'varchar(500)', 'NO', '', NULL, ''
    'regdate', 'timestamp', 'NO', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
     */
}
