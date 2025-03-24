## 파일구조 - Back
```
src
 └── main
     └── java
         └── com.project.react_tft
             ├── config                # 프로젝트 설정 관련 클래스
             │   ├── AwsConfig
             │   ├── CustomSecurityConfig
             │   ├── PasswordEncoderConfig
             │   ├── RootConfig
             │   └── SwaggerConfig
             │
             ├── controller             # API
             │   ├── advice
             │   │   └── CustomRestAdvice
             │   ├── BoardController
             │   ├── HomeController
             │   ├── MeetBoardController
             │   ├── MeetBoardImageController
             │   ├── MeetReplyController
             │   ├── MemberController
             │   ├── MovieController
             │   ├── ReplyController
             │   ├── ReviewController
             │   ├── SampleController
             │   └── UnloginController
             │
             ├── domain                 # 엔티티 클래스
             │   ├── BaseEntity
             │   ├── Board
             │   ├── MeetBoard
             │   ├── MeetBoardImage
             │   ├── MeetReply
             │   ├── Member
             │   ├── MemberRole
             │   ├── Movie
             │   ├── Reply
             │   └── Review
             │
             ├── dto                     # DTO (Data Transfer Object)
             │   ├── image
             │   │   ├── ImageFileDTO
             │   │   └── ImageResultDTO
             │   ├── BoardDTO
             │   ├── BoardListReplyCountDTO
             │   ├── MeetBoardDTO
             │   ├── MeetBoardImageDTO
             │   ├── MeetBoardListAllDTO
             │   ├── MeetBoardListReplyCountDTO
             │   ├── MeetReplyDTO
             │   ├── MemberDTO
             │   ├── MemberSecurityDTO
             │   ├── MovieDTO
             │   ├── PageRequestDTO
             │   ├── PageResponseDTO
             │   ├── ReplyDTO
             │   ├── ReviewDTO
             │   ├── ReviewPageRequestDTO
             │   └── ReviewPageResponseDTO
             │
             ├── Repository               # JPA Repository
             │   ├── BoardRepository
             │   ├── MeetBoardRepository
             │   ├── MeetReplyRepository
             │   ├── MemberRepository
             │   ├── MovieRepository
             │   ├── ReplyRepository
             │   └── ReviewRepository
             │
             ├── security                 # 보안 및 인증 관련
             │   ├── filter
             │   │   ├── exception
             │   │   ├── handler
             │   │   │   ├── LoginFilter
             │   │   │   ├── RefreshTokenFilter
             │   │   │   ├── TokenCheckFilter
             │   │   ├── CustomOauth2UserService
             │   │   └── CustomUserDetailsService
             │   ├── handler
             │   │   ├── CustomSocialLoginSuccessHandler
             │
             ├── service                   # serveice
             │   ├── BoardService
             │   ├── BoardServiceImpl
             │   ├── MeetBoardService
             │   ├── MeetBoardServiceImpl
             │   ├── MeetReplyService
             │   ├── MeetReplyServiceImpl
             │   ├── MemberService
             │   ├── MemberServiceImpl
             │   ├── MovieService
             │   ├── MovieServiceImpl
             │   ├── ReplyService
             │   ├── ReplyServiceImpl
             │   ├── ReviewService
             │   └── ReviewServiceImpl
             │
             ├── util                      
             │   └── JWTUtil               # JWT Util
             │
             └── ReactTftApplication       # 메인 애플리케이션 클래스
```
