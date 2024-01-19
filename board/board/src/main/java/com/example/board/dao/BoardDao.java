package com.example.board.dao;

import com.example.board.dto.Board;
import com.example.board.dto.User;
import com.example.board.service.BoardService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class BoardDao {

    private final NamedParameterJdbcTemplate jdbcTemplate; // sql를 실행하기 위한 spring jdbc를 이용하기 위한 코드
    private final SimpleJdbcInsertOperations insertBoard; // insert를 직접 적지 않기 위해 사용하는 인터페이스

    // 생성자 주입. Spring이 자동으로 HikariCP Bean을 주입한다.
    public BoardDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource); // dataSource를 생성자로 받아들인 NamedParameterJdbcTemplate 초기화
        insertBoard = new SimpleJdbcInsert(dataSource) //
                .withTableName("Board")
                .usingGeneratedKeyColumns("board_id "); // 자동으로 1씩 증가하는 아이디 설정
    }

    @Transactional
    public void addBoard(int userId, String title, String content) {
        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(board); // dto에 있는 값을 자동으로 sql 파라미터로 대입
        insertBoard.execute(params);
    }

    @Transactional(readOnly = true)
    public int getTotalCout() {
        String sql = "select count(*) as totalCount from board"; // 집합쿼리는 무조건 한 줄 데이터
        Integer totalCount = jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
        return totalCount.intValue();
    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        // start는 0, 10, 20, 30은 1page, 2page, 3page, 4page
        int start = (page - 1) * 10;
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name from board b, user u where b.user_id = u.user_id order by board_id desc limit 0, 10";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class); // Board 클래스에 담겨지는 RowMapper 자동 생성
        List<Board> list = jdbcTemplate.query(sql, Map.of("start", start), rowMapper);
        return list;
    }

    @Transactional(readOnly = true)
    public Board getBoard(int boardId) {

        // 값이 1건 또는 0건 나오는 쿼리
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name, b.content from board b, user u where b.user_id = u.user_id and b.board_id = :boardId";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class); // Board 클래스에 담겨지는 RowMapper 자동 생성
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }

    @Transactional
    public void updateViewcnt(int boardId) {
        String sql = "update board set view_cnt = view_cnt + 1 where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));

    }

    @Transactional
    public void deleteBoard(int boardId) {
        String sql = "delete from board where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        String sql = "update board set title = :title, content = :content where board_id = :boardId ";

        Board board = new Board();
        board.setBoardId(boardId);
        board.setTitle(title);
        board.setContent(content);
        SqlParameterSource parmas = new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql, parmas);
    }

}
