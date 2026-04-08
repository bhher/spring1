package board1.board.repository;

import board1.board.entity.Board;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends CrudRepository<Board,Long> {
    @Override
    List<Board> findAll();
    //extends 해온것에서 형태를 바꿔서 메소드 불러오고 싶을때 오버라이드
}
