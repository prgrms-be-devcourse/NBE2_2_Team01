# Blog_Web_Development

## 진행상황

### 회원가입

![image](https://github.com/user-attachments/assets/1ff10135-f2af-4b2b-8818-a75f9d3f00ad)
- http://localhost:8080/signup 회원가입 필요
- blogdb의 users테이블에 OAuth 이메일과 비밀번호가 있어야 다음이 진행됨-> 디비에 레코드가 없어도 돌아가는 방법이 없나?

### OAuth로 로그인

![image](https://github.com/user-attachments/assets/7fc39bfb-c75a-4f7c-a49b-c28c54cdd980)
- http://localhost:8080/login

### 구글 로그인하기

![image](https://github.com/user-attachments/assets/f45e0aa3-1d51-43c2-a7fc-c76fc928e749)
- users테이블에 있는 구글 이메일로 로그인

![image](https://github.com/user-attachments/assets/e4a93f67-35ab-40d6-9049-b1e3d4d5dfb7)

- 비밀번호 입력
- 이때 db에서 비밀번호는 암호화되어 있음

![image](https://github.com/user-attachments/assets/5fe73090-f741-4033-9b17-2e1fc2e92b15)
- 계속

![image](https://github.com/user-attachments/assets/a9bd5ca9-48f8-43ab-82be-c05b9d1d9751)

![image](https://github.com/user-attachments/assets/5bbd54c8-b274-48d0-b6fa-6bb40df700e2)
- 글 등록 가능

![image](https://github.com/user-attachments/assets/15b2df53-181b-43ec-9a38-960f48df71c8)
- 자신이 작성한(같은 이메일) 글이면 수정, 삭제 가능
  
![image](https://github.com/user-attachments/assets/d65b9408-e81a-4c90-9598-7398fd346e16)
- 다른 사용자가 작성한 글은 수정, 삭제 불가 ->다른 사용자와 본인을 구분해서 다른 사용자의 글은 수정, 삭제 버튼이 안보이게 할 순 없을까

### 카카오톡 로그인하기

![image](https://github.com/user-attachments/assets/9fe28100-ea9e-4fd4-9404-c4d7897698c6)
- users테이블에 있는 카카오톡 이메일로 로그인
- 이후 구글 로그인과 똑같이 됨

### 문제상황

1. nickname 컬럼명이 겹치면 안됨
   - 만약에 사용자가 구글, 카카오톡 로그인을 시도할때 이름(ex. 홍길동)이 같으므로 nickname에 본인 이름이 들어간다.
 ```
  //OAuth관련키 저장
    @Column(name="nickname",unique = true)
    private String nickname;

    //생성자에 nickname 추가
    @Builder
    public User(String email, String password,String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    //사용자 이름 변경
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }
 ```
  - 위 코드는 User 클래스 일부분
```
 private User savedOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes(); //OAuth2 사용자 정보(email, name 등)를 속성 맵으로 가져온다.

        // OAuth2User의 속성 출력
        String email;
        String name;

        if (attributes.containsKey("kakao_account")) {
            email = (String) ((Map<String, Object>) attributes.get("kakao_account")).get("email");
            System.out.println("Email from Kakao: " + email);
            name = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");

        } else {
            // 구글 사용자 정보 처리
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        User user = userRepository.findByEmail(email)
                .map(entity -> {
                    // 로그 추가
                    System.out.println("User found, updating: " + entity);
                    return entity.update(name);
                })
                ...
;
        return userRepository.save(user); //새로운 사용자 정보를 저장하거나 업데이트된 사용자 정보를 저장한다.
    }
```
- 위 코드는 Oauth2UserCustomService 클래스 일부분

![image](https://github.com/user-attachments/assets/7a1f11e9-aaa9-4fd0-94e6-950f15b47f33)
- 같은 nickname으로 로그인시 에러
  
- 결론: 같은 사용자가 구글계정에서 카카오톡 계정으로 로그인하기 위해 구글 계정 로그아웃할때, nickname이 본인 이름이 아닌 null로 업데이트되게 만들고 싶음(실패)

  2.  
