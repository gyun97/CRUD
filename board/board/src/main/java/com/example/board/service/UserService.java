package com.example.board.service;


import com.example.board.dao.UserDao;
import com.example.board.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 트랜잭션 단위로 실행될 메소드를 선언하고 있는 클래스이다.
@Service // 트랙잭션 단위로 비즈니스를 처리해주는 Bean (Spring이 관리해주는 Bean)
@RequiredArgsConstructor // lombok이 final 필드를 초기화하는 생성자를 자동으로 생성
public class UserService {

    private final UserDao userDao; //injection되는 Dao는 보통 final 선언 -> final이여서 생성자 주입

//     Spring이 UserService를 Bean으로 생성할 때 생성자를 이용해 생성하는데
//     이 때 UserDao Bean이 있는지 보고 그 Bean을 주입
//    public UserService(UserDao userDao) {
//        this.userDao = userDao;
//    }


    // 보통 서비스가 갖고 있는 메서드에서는 @Transactional을 붙여 하나의 트랜잭션으로 처리
    // Spring Boot에서는 트랜잭션을 처리해주는 AOP라는 트랜잭션 관리자가 존재
    @Transactional
    public User addUser(String name, String email, String password) {

        User user1 = userDao.getUser(email); // 이메일 중복 검사
        if (user1 != null) {
            throw new RuntimeException("이미 가입된 아이디입니다.");
        }

    /*
    insert into user (email, name, password, regdate) values (?, ?, ?, now()); # AI로 유저 아이디 자동 생성
    select last_insert_id();
    insert into user_role(user_id, role_id) values (?, 1);

     해당 쿼리문들 실행 후 해당 정보들을 User에 담아서 리턴.
     sql이 실행되는 객체는 'dao'라고 한다.
     */

        User user = userDao.addUser(email, name, password);
        userDao.mappingUserRole(user.getUserId()); //권한을 부여
        return user;

    }

    @Transactional
    public User getUser(String email) {
        return userDao.getUser(email);
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        return userDao.getRoles(userId);
    }
}
