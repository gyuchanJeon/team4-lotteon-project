package com.lotte4.service.board;


import com.lotte4.dto.BoardCommentDTO;
import com.lotte4.dto.BoardRegisterDTO;
import com.lotte4.dto.BoardResponseDTO;
import com.lotte4.entity.Board;
import com.lotte4.entity.BoardCate;
import com.lotte4.repository.UserRepository;
import com.lotte4.repository.board.BoardCateRepository;
import com.lotte4.repository.board.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class BoardService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardCateRepository boardCateRepository;
    private final ModelMapper modelMapper;

    // 글 타입별로 찾기 ( qna | faq | notice )
    public Page<BoardResponseDTO> selectAllBoardByType(String type, int page, int size) {


        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boardEntities = boardRepository.findByTypeOrderByRegDateDesc(type, pageable);
        return applyRowNumber(boardEntities, pageable, page, size);
    }

    public Page<BoardResponseDTO> selectArticleByQnaAndUid(String type, String uid, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boardEntities = boardRepository.findByTypeAndUser_UidOrderByRegDateDesc(type, uid, pageable);

        return applyRowNumber(boardEntities, pageable, page, size);
    }


    // 카테고리 아이디로 글 찾기 (부모 카테고리, 자식 카테고리 공용)
    public Page<BoardResponseDTO> selectAllBoardByCateId(int cateId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boardEntities;
        Optional<BoardCate> category = boardCateRepository.findById(cateId);

        if (category.isPresent()) {
            // 부모가 null - 부모 카테고리 (depth=1) => board의 카테고리의 상위로 조회해야함 (ex) '회원' 으로 조회)
            if (category.get().getParent() == null) {
                boardEntities = boardRepository.findByCate_Parent_BoardCateIdAndTypeOrderByRegDateDesc(cateId, type, pageable);
            } else {
            // 부모가 존재 - 자식 카테고리 (depth=2) => board의 카테고리로 조회해야함 (ex) '회원 가입' 으로 조회)
                boardEntities = boardRepository.findByCate_BoardCateIdAndTypeOrderByRegDateDesc(cateId, type, pageable);
            }

        } else {

            throw new IllegalArgumentException("해당 게시글 카테고리 아이디로 조회된 카테고리가 없습니다.: " + cateId);
        }

        return applyRowNumber(boardEntities, pageable, page, size);
    }

    // applyRowNumber - 게시글 번호 인덱스를 넣기
    private Page<BoardResponseDTO> applyRowNumber(Page<Board> boardEntities, Pageable pageable, int page, int size) {
        int totalElements = (int) boardEntities.getTotalElements();
        AtomicInteger startNumber = new AtomicInteger(totalElements - (page * size));

        List<BoardResponseDTO> boardListWithRowNumber = boardEntities.stream()
                .map(board -> {
                    BoardResponseDTO dto = modelMapper.map(board, BoardResponseDTO.class);
                    dto.setRowNumber(startNumber.getAndDecrement()); // rowNumber 설정
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(boardListWithRowNumber, pageable, boardEntities.getTotalElements());
    }

    public Board insertBoard(BoardRegisterDTO dto) {
        log.info("insert board qna:" +dto);
        return userRepository.findByUid(dto.getUid())
                .map(user -> {
                    Board board = modelMapper.map(dto, Board.class);
                    board.setUser(user);

                    BoardCate category = boardCateRepository.findById(dto.getCate())
                            .orElseThrow(() -> new NoSuchElementException("Category not found"));
                    board.setCate(category);
                    log.info("BoardService insert board : "+board);
                    return boardRepository.save(board);
                })
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Transactional
    public Board updateBoard(BoardRegisterDTO dto) {
        Board existingboard = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 게시판을 찾을 수 없습니다: " + dto.getBoardId()));
        if(dto.getCate() != existingboard.getCate().getBoardCateId()){
            existingboard.setCate(boardCateRepository.findByBoardCateId(dto.getCate()));
        }
        if(dto.getTitle() != existingboard.getTitle()){
            existingboard.setTitle(dto.getTitle());
        }
        if(dto.getContent() != existingboard.getContent()){
            existingboard.setContent(dto.getContent());
        }
        return boardRepository.save(existingboard);
    }

    public BoardResponseDTO selectBoardById(int id) {
        return boardRepository.findById(id)
                .map(board -> modelMapper.map(board, BoardResponseDTO.class))
                .orElse(null);
    }

    public void insertBoardQnaComment(BoardCommentDTO commentDTO) {
        Optional<Board> existingBoard =boardRepository.findById(commentDTO.getBoardId());
        if(existingBoard.isPresent()){
            Board board = existingBoard.get();
            board.setComment(commentDTO.getComment());
            board.setState(1); // 답변완료로 상태 수정
            boardRepository.save(board);
        }
    }

    public boolean deleteBoardByBoardId(int boardId) {
        try {
            boardRepository.deleteById(boardId);
            return true; // 성공적으로 삭제된 경우
        } catch (EmptyResultDataAccessException e) {
            // 존재하지 않는 boardId로 삭제 시도 시 예외 발생
            System.out.println("해당 ID의 게시글이 존재하지 않습니다: " + boardId);
            return false; // 삭제 실패
        } catch (Exception e) {
            // 다른 예외 처리
            System.out.println("삭제 중 오류가 발생했습니다: " + e.getMessage());
            return false; // 삭제 실패
        }
    }

    public List<Board> findTop5ByOrderByRegdateDesc(String type) {
        return boardRepository.findTop5ByTypeOrderByRegDateDesc(type);
    }
    public Board findById(int boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with ID: " + boardId));
    }

    public int countBoardWithDay(LocalDate localDate) {
        return boardRepository.findAllByDay(localDate);
    }

}
