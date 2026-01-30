package com.trading.realtimetrading.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; // List 추가

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 생성 (UserController.createUser에서 사용)
     */
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * ID로 사용자 조회 (UserController.getUser에서 사용)
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 모든 사용자 조회 (UserController.getAllUsers에서 사용)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 사용자 정보 수정 (UserController.updateUser에서 사용)
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        // User 엔티티 내에 update 메서드가 있다고 가정 (예: 닉네임 등 변경)
        user.updateNickname(userDetails.getNickname());
        return user;
    }

    /**
     * 사용자 삭제 (UserController.deleteUser에서 사용)
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // 기존에 있던 메서드들
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }

    @Transactional
    public void updateNickname(String email, String newNickname) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        user.updateNickname(newNickname);
    }
}