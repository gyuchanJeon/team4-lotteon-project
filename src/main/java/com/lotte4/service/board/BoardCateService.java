package com.lotte4.service.board;

import com.lotte4.dto.BoardCateDTO;
import com.lotte4.entity.BoardCate;
import com.lotte4.repository.board.BoardCateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class BoardCateService {

    private final BoardCateRepository boardCateRepository;
    private final ModelMapper modelMapper;

    public List<BoardCateDTO> selectBoardCatesByDepth(int depth) {
        List<BoardCate> categories = boardCateRepository.findBoardCateByDepth(depth);

        return categories.stream()
                .map(category -> modelMapper.map(category, BoardCateDTO.class))
                .collect(Collectors.toList());
    }

    ;

    public BoardCateDTO getCategories(int id) {
        BoardCate boardCate = boardCateRepository.findByBoardCateId(id);

        return modelMapper.map(boardCate, BoardCateDTO.class);
    }

    public List<BoardCateDTO> getSubCategories(int parentId) {
        List<BoardCate> subCategories = boardCateRepository.findByParentId(parentId);
        return subCategories.stream()
                .map(subCategory -> modelMapper.map(subCategory, BoardCateDTO.class))
                .collect(Collectors.toList());
    }
}