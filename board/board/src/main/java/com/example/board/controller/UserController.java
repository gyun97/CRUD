package com.example.board.controller;

import com.example.board.dto.Logininfo;
import com.example.board.dto.User;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@RequiredArgsConstructor
@Controller // 웹에 관해 처리해주는 Bean
public class UserController {

    private final UserService userService;


    // http://localhost8080/userRegFrom -> classpath:/templates/userRegForm.html
    @GetMapping("userRegForm")
    public String userRegForm() {
        return "userRegForm";
    }

    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        // email에 해당하는 회원 정보를 읽어온 후,
        // 아이디가 맞다면 세션에 회원정보를 저장.

        System.out.println("name = " + name);
        System.out.println("email = " + email);
        System.out.println("password = " + password);

        userService.addUser(name, email, password);

        return "redirect:/welcome"; // 브라우져가 자동으로 http://localhost8080/welcome으로 이동

    }

    // http://localhost8080/welcome로 이동
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }


    @GetMapping("/loginform")
    public String loginform() {
        return "loginform";
    }

    @PostMapping("/login")
    public String login(
//            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession // Spring이 자동으로 처리하는 session을 처리하는 HttpSession 객체를 넣어준다.
    ) {
        /*이메일에 해당하는 회원 정보를 읽어온 후 아이디 암호가 맞다면 세션에 회원정보 저장*/
        System.out.println("email = " + email);
        System.out.println("password = " + password);


        try {
            User user = userService.getUser(email); //회원 정보를 저장한다. 동사는 메소드가, 메소드가 선언하고 있는 것은 인터페이스가
            if (user.getPassword().equals(password)) {
                System.out.println("암호가 같습니다");
                Logininfo logininfo = new Logininfo(user.getUserId(), user.getEmail(), user.getName());

                // 권한정보를 읽어와서 loginInfo에 추가한다.
                List<String> roles = userService.getRoles(user.getUserId());
                logininfo.setRoles(roles);

                httpSession.setAttribute("loginInfo", logininfo);
                System.out.println("세션에 로그인 정보가 저장되었습니다.");
            } else {
                throw new RuntimeException("암호가 일치하지 않습니다.");
            }

            System.out.println(user);
        } catch (Exception ex) {
            return "redirect:/loginform?error=true";
        }


        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        //세션에서 회원 정보 삭제
        httpSession.removeAttribute("loginInfo");
        return "redirect:/";
    }


}


