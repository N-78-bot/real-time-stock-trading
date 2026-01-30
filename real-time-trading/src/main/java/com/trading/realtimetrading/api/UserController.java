package com.trading.realtimetrading.api;

import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 생성 (관리자용 - 실제로는 Role 체크 필요)
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * 내 정보 조회 (인증된 사용자 본인)
     */
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * 특정 사용자 조회 (관리자용 - 실제로는 Role 체크 필요)
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 모든 사용자 조회 (관리자용 - 실제로는 Role 체크 필요)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 내 정보 수정 (닉네임 변경)
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateMyInfo(
            Authentication authentication,
            @RequestBody User userDetails) {

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        User updatedUser = userService.updateUser(user.getId(), userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 특정 사용자 수정 (관리자용 - 실제로는 Role 체크 필요)
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {

        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 회원 탈퇴 (본인만)
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 사용자 삭제 (관리자용 - 실제로는 Role 체크 필요)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}