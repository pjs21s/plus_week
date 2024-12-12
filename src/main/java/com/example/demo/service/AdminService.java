package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: 4. find or save 예제 개선
    /**
     * DB 접근 최소화를 위하여 userIds 의 List 에 있는 id 들을 userRepository.findAllById 로 한번에 가져올 수 있습니다.
     * 이후 user 의 상태를 변경해줍니다.
     * 마지막으로 saveAll 메소드를 이용하여 변경한 user 전체를 저장합니다.
     */
    @Transactional
    public void reportUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
//            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));

            user.updateStatusToBlocked();

//            userRepository.save(user);
        }
        userRepository.saveAll(users);
    }
}
