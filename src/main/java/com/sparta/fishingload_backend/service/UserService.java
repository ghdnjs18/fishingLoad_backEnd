package com.sparta.fishingload_backend.service;

import com.sparta.fishingload_backend.dto.*;
import com.sparta.fishingload_backend.entity.Post;
import com.sparta.fishingload_backend.entity.User;
import com.sparta.fishingload_backend.entity.UserRoleEnum;
import com.sparta.fishingload_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "A1234";

    // 회원가입
    public ResponseEntity<MessageResponseDto> signup(SignupRequestDto requestDto) {
        String userId = requestDto.getUserId();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUserId(userId);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 사용자 권한 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 사용자 등록
        User user = new User(userId, password, role, requestDto);
        userRepository.save(user);

        MessageResponseDto message = new MessageResponseDto("회원가입이 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    //아이디 찾기
    public FindUserResponseDto findUser(FindRequestDto findRequestDto) {
        String userId = UserIdFind(findRequestDto.getEmail()).getUserId();
        FindUserResponseDto findUserResponseDto = new FindUserResponseDto(userId);
        return findUserResponseDto;
    }

    //비밀번호 찾기
    public FindPasswordResponseDto findPassword(FindRequestDto findRequestDto) {
        String password = PasswordFind(findRequestDto.getEmail() , findRequestDto.getUserId()).getPassword();
        FindPasswordResponseDto findPasswordResponseDto = new FindPasswordResponseDto(password);
        return findPasswordResponseDto;
    }



    // 회원 탈퇴
    @Transactional
    public ResponseEntity<MessageResponseDto> resign(User user) {
        User userselect = userRepository.findByUserId(user.getUserId()).orElse(null);

//         현재 게시물과 댓글이 없기 때문에 주석 처리함 이후 사용할  예정
        // 회원이 작성한 게시물 삭제 처리
        for (Post board : userselect.getPostList()) {
            board.setPostUse(false);
//            for (Comment comment : board.getCommentList()) {
//                comment.setCommentUse(false);
//            }
        }
//        // 회원이 작성한 댓글 삭제 처리
//        for (Comment comment : userselect.getCommentList()) {
//            comment.setCommentUse(false);
//        }
        userselect.setAccountUse(false);

        MessageResponseDto message = new MessageResponseDto("회원탈퇴가 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    private User UserIdFind(String email) {
        return userRepository.findByEmailAndAccountUseTrue(email).orElseThrow(
                () -> new NullPointerException("해당 유저는 존재하지 않습니다."));
    }

    private User PasswordFind (String userId, String email) {
        return userRepository.findByUserIdAndEmail(userId,email).orElseThrow(
                () -> new NullPointerException("해당 비밀번호가 존재하지 않습니다. "));
    }

}