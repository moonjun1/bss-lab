package com.bsslab.domain.auth.controller;

import com.bsslab.domain.auth.dto.AuthDto;
import com.bsslab.domain.auth.service.AuthService;
import com.bsslab.global.exception.GlobalExceptionHandler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 관리", description = "회원가입, 로그인 등 사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다. 아이디, 이메일, 비밀번호를 입력해주세요."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "{\"message\": \"User registered successfully!\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 아이디 또는 이메일",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"Username is already taken!\", \"path\": \"/api/auth/signup\", \"timestamp\": \"2023-01-01T12:00:00\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"Validation failed\", \"timestamp\": \"2023-01-01T12:00:00\", \"errors\": {\"username\": \"Username is required\", \"email\": \"Email should be valid\"}}")
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody AuthDto.SignupRequest signupRequest) {
        authService.registerUser(signupRequest);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호로 로그인하여 JWT 토큰을 받습니다. 받은 토큰은 다른 API 호출 시 Authorization 헤더에 'Bearer [토큰값]' 형식으로 포함해야 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthDto.TokenResponse.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwiaWF0IjoxNjQ2OTIwMjQ4LCJleHAiOjE2NDcwMDY2NDh9.abcdef123456\", \"type\": \"Bearer\", \"id\": 1, \"username\": \"user123\", \"email\": \"user@example.com\", \"role\": \"ROLE_USER\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 잘못된 아이디 또는 비밀번호",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\": 401, \"message\": \"Invalid username or password\", \"path\": \"/api/auth/login\", \"timestamp\": \"2023-01-01T12:00:00\"}")
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> authenticateUser(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        AuthDto.TokenResponse tokenResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }
}