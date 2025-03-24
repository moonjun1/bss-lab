# BSS-Lab 백엔드 프로젝트

BSS-Lab 소개 페이지를 위한 Spring Boot 기반 백엔드 애플리케이션입니다. 사용자 인증, 게시판 시스템, 관리자 대시보드, 연구실 지원 시스템 등의 기능을 제공합니다.

## 기술 스택

- **백엔드 프레임워크**: Spring Boot 3.2.3
- **데이터베이스**: MySQL
- **인증**: Spring Security, JWT
- **ORM**: JPA/Hibernate, Spring Data JPA
- **API 문서화**: Swagger (SpringDoc OpenAPI)
- **빌드 도구**: Gradle
- **개발 언어**: Java 17

## 폴더 구조

도메인 중심 설계를 기반으로 한 프로젝트 구조입니다:

```
com.bsslab (루트 패키지)
├── 공통 기능 (common)
├── 각 기능별 도메인 (domain)
│   ├── 인증 기능 (auth)
│   ├── 사용자 기능 (user)
│   └── 앞으로 만들 게시판 등 기능
└── 전역 설정 (global)
    ├── 설정 (config)
    ├── 예외 처리 (exception)
    └── 보안 (security)
```

### 세부 파일 구조

```
com.bsslab
├── BssLabApplication.java (메인 애플리케이션 클래스)
├── common
│   └── entity
│       └── BaseTimeEntity.java (공통 시간 Entity)
├── domain
│   ├── auth
│   │   ├── controller
│   │   │   └── AuthController.java (인증 컨트롤러)
│   │   ├── dto
│   │   │   └── AuthDto.java (인증 관련 DTO)
│   │   └── service
│   │       └── AuthService.java (인증 서비스)
│   └── user
│       ├── entity
│       │   ├── User.java (사용자 엔티티)
│       │   └── UserProfile.java (사용자 프로필 엔티티)
│       └── repository
│           ├── UserRepository.java (사용자 저장소)
│           └── UserProfileRepository.java (사용자 프로필 저장소)
└── global
    ├── config
    │   └── SwaggerConfig.java (Swagger 설정)
    ├── exception
    │   ├── DuplicateResourceException.java (중복 리소스 예외)
    │   └── GlobalExceptionHandler.java (전역 예외 처리기)
    └── security
        ├── config
        │   └── SecurityConfig.java (Spring Security 설정)
        ├── jwt
        │   ├── JwtAuthenticationFilter.java (JWT 인증 필터)
        │   ├── JwtProperties.java (JWT 속성)
        │   └── JwtUtils.java (JWT 유틸리티)
        └── service
            └── CustomUserDetailsService.java (사용자 상세 서비스)
```

## 주요 기능

- **사용자 인증 및 관리**
  - 회원가입 및 로그인/로그아웃
  - JWT 기반 인증
  - 사용자 프로필 관리

- **게시판 시스템** (개발 예정)
  - 게시글 CRUD
  - 이미지 첨부 기능
  - 카테고리별 게시글 분류

- **관리자 대시보드** (개발 예정)
  - 회원 관리
  - 게시글 관리
  - 시스템 통계

- **연구실 지원 시스템** (개발 예정)
  - 지원서 제출 및 관리
  - 지원 서류 업로드
  - 지원 현황 조회

## API 문서

- 서버 실행 후 `http://localhost:8080/api/swagger-ui.html`에서 API 문서 확인 가능

## 개발 환경 설정

### 요구사항

- JDK 17 이상
- MySQL 8.0 이상
- Gradle 7.0 이상

### 실행 방법

1. 프로젝트 클론
   ```bash
   git clone https://github.com/moonjun1/bss-lab.git
   cd bsslab-backend
   ```

2. MySQL 데이터베이스 생성
   ```sql
   CREATE DATABASE bsslab;
   ```

3. `application.properties` 파일 수정
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bsslab?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

4. 애플리케이션 빌드 및 실행
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

5. API 테스트
   - Swagger UI: `http://localhost:8080/api/swagger-ui.html`
   - API 엔드포인트: `http://localhost:8080/api/...`

## 로드맵

1. 인증 및 사용자 관리 시스템 (완료)
2. 게시판 시스템 (개발 중)
3. 관리자 대시보드 (예정)
4. 연구실 지원 시스템 (예정)

## 라이센스

MIT License
