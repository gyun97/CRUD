package com.example.board.dao;

import com.example.board.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCallOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Repository // 데이터를 처리해주는 Bean
public class UserDao {

    private final NamedParameterJdbcTemplate jdbcTemplate; // spring jdbc를 이용하기 위한 코드
    private final SimpleJdbcInsertOperations insertUser; // insert를 직접 적지 않기 위해 사용

    // final 사용으로 인한 생성자로  jdbcTemplate 값 초기화
    public UserDao(DataSource dataSource) {
         jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id"); // 자동으로 1씩 증가하는 아이디 설정
    }


    // Spring JDBC를 이용한 코드 작성

    @Transactional
    // 두 개의 쿼리가 실행되었지만 하나의 트랜잭션으로 처리하기 위해 @Transactional
    public User addUser(String email, String name, String password) {
        //insert into user (email, name, password, regdate) values (:name, :email, :password, :redgate);
        //        select last_insert_id();

        User user = new User();
        user.setName(name); // name 컬럼에 값 대입
        user.setEmail(email); // emial 컬럼에 값 대입
        user.setPassword(password); // password 컬럼에 값 대입
        user.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(user); // dto에 있는 값을 자동으로 sql 파라미터로 대입
        Number number = insertUser.executeAndReturnKey(params);// insert를 실행하고, 자동으로 생성된 id를 가져온다.
        int userId = number.intValue();
        user.setUserId(userId);
        return user;
    }

    @Transactional
    public void mappingUserRole(int userId) {
        // insert into user_role(user_id, role_id) values (?, 1);

        String sql = "insert into user_role(user_id, role_id) values (:userId, 1);";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        jdbcTemplate.update(sql, params);

    }


    @Transactional
    public User getUser(String email) {
        try{
        // user_id => setUserid, email => setEmail
        String sql = "select user_id, email, name, password, regdate from user where email = email";

        SqlParameterSource params = new MapSqlParameterSource("email", email);
        RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
        User user = jdbcTemplate.queryForObject(sql, params, rowMapper);
        return user;
    }
        catch(Exception ex){
        return null;
        }
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        String sql = "select r.name from user_role ur, role r where ur.role_id = r.role_id and ur.user_id = :userId";

        List<String> roles = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
            return rs.getString(1);
        });

        return roles;
    }

}
