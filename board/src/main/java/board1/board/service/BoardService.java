package board1.board.service;

import board1.board.entity.Board;
import board1.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public Optional<Board> findById(Long id) {
        return boardRepository.findById(id);
    }

    public void deleteById(Long id) {
        boardRepository.deleteById(id);
    }
}
