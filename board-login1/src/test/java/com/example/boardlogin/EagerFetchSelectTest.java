package com.example.boardlogin;

import com.example.boardlogin.domain.Board;
import com.example.boardlogin.domain.User;
import com.example.boardlogin.repository.BoardRepository;
import com.example.boardlogin.repository.UserRepository;
import com.example.boardlogin.service.BoardService;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EagerFetchSelectTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void eagerFetch_usesMultipleSelectQueries() {
        // author 3명을 각각 다른 board에 연결해서,
        // board 1번 조회 + author 조회가 여러 번 발생하는지(로그 확인) 보여줍니다.

        User u1 = userRepository.save(new User("u1", "pw1", "User1"));
        User u2 = userRepository.save(new User("u2", "pw2", "User2"));
        User u3 = userRepository.save(new User("u3", "pw3", "User3"));

        boardRepository.save(new Board("t1", "c1", u1, LocalDateTime.now()));
        boardRepository.save(new Board("t2", "c2", u2, LocalDateTime.now()));
        boardRepository.save(new Board("t3", "c3", u3, LocalDateTime.now()));

        // 영속성 컨텍스트를 비워서(캐시 제거)
        // author가 "이미 메모리에 있어서" 추가 SQL이 안 나가는 상황을 방지합니다.
        entityManager.flush();
        entityManager.clear();

        List<Board> boards = boardService.findAll();

        // author에 접근해서 실제 초기화가 일어나는 것을 확실히 합니다.
        boards.forEach(b -> b.getAuthor().getUsername());

        assertEquals(3, boards.size());
    }
}

