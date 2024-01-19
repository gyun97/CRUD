package com.example.board.controller;

import com.example.board.dto.Board;
import com.example.board.dto.Logininfo;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller // http 요청을 받아서 응답해주는 컴포넌트. @Component가 들어있기 때문에 스프링 부트가 자동으로 빈 생성
@RequiredArgsConstructor // final인 변수 자동 초기화
public class BoardController {

    private final BoardService boardService; // final 선언한 것은 값을 생성자에서 주입받아야 함


    // 게시물 목록을 보여준다.
    // Controller의 메서드가 리턴하는 문자열은 템플릿의 이름이다.
    // http://localhost8080/ 접속 -> 서버에 해당 메서드가 호출 -> 템플릿 화면 출력

    @GetMapping("/") // "/" 요청이 오면 자동으로 해당 메서드가 실행

    public String list(@RequestParam(name = "page", defaultValue = "1") int page, HttpSession session, Model model) { // HttpSession, Model은 Spring이 자동으로 넣어준다.

        //게시글 목록을 읽어오고 페이징 처리 필요

        //HttpSession은 세션 처리를 위한 객체, Model은 템플릿에 값을 넘기기 위한 객체
        Logininfo loginInfo = (Logininfo) session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo); // 템플릿에게 loginInfo라는 키값을 넘긴다

        int totalCount = boardService.getTotalCount();
        List<Board> list = boardService.getBoards(page); // page는 1 -> 2 -> 3 ...

        int pageCount = totalCount / 10;
        if (totalCount % 10 > 0) { // 나머지가 있을 경우 1페이지 추가
            pageCount++;
        }
        int currentPage = page;

        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);
        return "list"; // list라는 이름의 템플릿을 리턴하여 해당 템플릿을 사용하여(forward) 화면에 출력

    }

    //board?=id=3 // id가 파라미터
    @GetMapping("/board")
    public String board(@RequestParam("boardId") int boardId, Model model) {
        System.out.println("boardId = " + boardId);

        // id에 해당하는 게시물 읽어오기.
        // id에 해당하는 게시글의 조회수 1 증가

        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board); // model을 통해 템플릿에 값 전달해서 화면에 표시

        return "board";
    }


    // 관리자가 모든 게시글을 삭제할 수 있는 권한을 갖도록 설정한다.
    // 게시글 작성자가 자신의 게시글을 수정할 수 있도록 설정한다.

    @GetMapping("/writeForm")
    public String writeForm(HttpSession session, Model model) {
        // 로그인한 사용자만 글쓰기 가능하다.  비로그인 상태라면 리스트 보기로 자동 이동시킨다.
        // 누가 글을 작성하였는지 알기 위해 세션에서 로그인한 정보를 읽어들인다.
        Logininfo loginInfo = (Logininfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform"; // 세션에 로그인 정보 없으면 /loginform으로 redirect
        }


        model.addAttribute("loginInfo", loginInfo); // 세션에 값 있으면 model에 loginInfo 정보 담아서 전달

        return "writeForm";
    }

    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session

    ) {

        Logininfo loginInfo = (Logininfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform"; // 세션에 로그인 정보 없으면 /loginform으로 redirect
        }
        // 로그인한 사용자만 글쓰기 가능하다.  비로그인 상태라면 리스트 보기로 자동 이동시킨다.
        // 누가 글을 작성하였는지 알기 위해 세션에서 로그인한 정보를 읽어들인다.

        // 로그인한 회원 정보 및 제목, 내용을 저장한다.
        System.out.println("title =" + title);
        boardService.addBoard(loginInfo.getUserId(), title, content);

        return "redirect:/"; // 리스트 보기로 이동


    }

    @GetMapping("/delete")
    public String delete(@RequestParam("boardId") int boardId, HttpSession session) {

        Logininfo loginInfo = (Logininfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform"; // 세션에 로그인 정보 없으면 /loginform으로 redirect
        }

        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else{
            boardService.deleteBoard(loginInfo.getUserId(), boardId );
        }


        // loginInfo.getUserId() 사용자가 쓴 글일 경우에만 삭제한다
        boardService.deleteBoard(loginInfo.getUserId(), boardId);
        return "redirect:/";

    }

    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId") int boardId, Model model, HttpSession session) {

        // 로그인 했는지 확인, 안 했으면 로그인 폼으로 이동
        Logininfo loginInfo = (Logininfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform"; // 세션에 로그인 정보 없으면 /loginform으로 redirect
        }

        // boardId에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달한다.
        Board board = boardService.getBoard(boardId, false);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";

    }

    @PostMapping("/update")
    public String update(@RequestParam("boardId") int boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session
    ) {

        // 로그인 했는지 확인, 안 했으면 로그인 폼으로 이동
        Logininfo loginInfo = (Logininfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform"; // 세션에 로그인 정보 없으면 /loginform으로 redirect
        }

        Board board = boardService.getBoard(boardId, false);
        if (board.getUserId() != loginInfo.getUserId()) {
            return "redirect:/board?boardId=" + boardId; // 글보기로 이동한다
        }

        // 글쓴이만 수정 가능
        // boardId에 해당하는 글의 제목과 내용을 수정한다.
        boardService.updateBoard(boardId, title, content);
        return "redirect:/board?boardId=" + boardId; // 수정된 글 보기로 리다이렉트

        
    }
}
