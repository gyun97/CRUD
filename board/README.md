# 게시판 만들기

## 기술 스택

1. Spring Boot
2. Spring MVC
3. MYSQL
4. thymeleaf(템플릿 엔진)

## 아키텍쳐
```

       Spring Core 사용(객체들 Bean으로 관리)
       

                Spring MVC          Spring JDBC  MySQL
[브라우저 요청 → Controller → Service] → [DAO] →   [DB]
                                                    ↓ 
  브라우저 응답 ←  템플릿 ←  Controller ← Service ←  DAO


(layer간의 데이터 전송은 DTO 이용)
```

## 게시판 작성 순서
1. Controller와 템플릿 연결
2. Service 생성 - 비즈니스 로직 처리(하나의 트랜잭션 단위)
3. Service는 데이터 처리를 위한 데이터 CRUD를 위해 DAO 사용