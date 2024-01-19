package com.example.board.service;

import com.example.board.dao.BoardDao;
import com.example.board.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardDao boardDao;


    @Transactional
    // 받아들인 파라미터들을 게시글에 저장하는 메서드
    public void addBoard(int userId, String title, String content) {
        boardDao.addBoard(userId, title, content);

    }

    @Transactional(readOnly = true) // select만 할 때에는 성능을 위해 readOnly = true
    public int getTotalCount() {
        return boardDao.getTotalCout();
    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        return boardDao.getBoards(page);

    }

    // updateViewCnt가 true면 글의 조회수를 증가, false면 글의 조회수를 증가하지 않도록 한다.
    @Transactional
    public Board getBoard(int boardId) {
        // id에 해당하는 게시글을 읽어준다.
        // id에 해당하는 게시글의 조회 수를 1 증가시킨다

        return getBoard(boardId, true);
    }

    // updateViewCnt가 true면 글의 조회 수 1 증가, false이면 조회 수 증가 X
    @Transactional
    public Board getBoard(int boardId, boolean updateViewCnt) {
        Board board = boardDao.getBoard(boardId);
        if (updateViewCnt) {
            boardDao.updateViewcnt(boardId);
        }
        return board;

    }

    @Transactional
    public void deleteBoard(int userId, int boardId) {
        Board board = boardDao.getBoard(boardId);
        if (board.getUserId() == userId) {
            boardDao.deleteBoard(boardId);
        }
    }

    @Transactional
    public void deleteBoard(int boardId) {
            boardDao.deleteBoard(boardId);
        }



    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        boardDao.updateBoard(boardId, title, content);
    }
}

