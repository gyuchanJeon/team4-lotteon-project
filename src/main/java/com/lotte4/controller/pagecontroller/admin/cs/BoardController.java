package com.lotte4.controller.pagecontroller.admin.cs;

import com.lotte4.dto.BoardCateDTO;
import com.lotte4.dto.BoardCommentDTO;
import com.lotte4.dto.BoardRegisterDTO;
import com.lotte4.dto.BoardResponseDTO;
import com.lotte4.entity.Board;
import com.lotte4.service.board.BoardCateService;
import com.lotte4.service.board.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardCateService boardCateService;
    private final BoardService boardService;
    // 관리자 cs - Board (qna,faq,notice)

    // admin 글 목록
    @GetMapping("/admin/cs/{type}/list")
    public String AdminQnaList(
            Model model,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        if (Objects.equals(type, "notice")) {

            List<BoardCateDTO> cates = boardCateService.getSubCategories(8);
            model.addAttribute("cates", cates);

        }
        if (Objects.equals(type, "faq") || Objects.equals(type, "qna")) {
            List<BoardCateDTO> cate1 = boardCateService.selectBoardCatesByDepth(1);
            model.addAttribute("cate1", cate1);
        }

        Page<BoardResponseDTO> boardList = boardService.selectAllBoardByType(type, page, size);
        model.addAttribute("boardList", boardList.getContent());
        model.addAttribute("totalPages", boardList.getTotalPages());
        model.addAttribute("currentPage", page);

        return "/admin/cs/" + type + "/list";
    }
    @ResponseBody
    @GetMapping("/board/{type}/{cate}")
    public ResponseEntity<Page<BoardResponseDTO>> selectBoard(
            @PathVariable(required = false) int cate,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Page<BoardResponseDTO> boardList = boardService.selectAllBoardByCateId(cate,type, page, size);

        return ResponseEntity.ok(boardList);
    }

    // 글보기 - qna,fap
    @GetMapping("/admin/cs/{type}/view/{id}")
    public String adminQnaView(Model model, @PathVariable int id, @PathVariable String type) {
        model.addAttribute("board", boardService.selectBoardById(id));

        return "/admin/cs/"+type+"/view";
    }

    // faq 글쓰기
    @GetMapping("/admin/cs/faq/write")
    public String AdminCsFaqList(Model model){
        List<BoardCateDTO> cate1 = boardCateService.selectBoardCatesByDepth(1);
        model.addAttribute("cate1", cate1);
        return "/admin/cs/faq/write";
    }

    @ResponseBody
    @DeleteMapping("/admin/cs/board/delete/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable int boardId) {
        boolean isDeleted = boardService.deleteBoardByBoardId(boardId);

        if (isDeleted) {
            return ResponseEntity.ok("Board deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete the board.");
        }
    }


    // faq 수정 - get
    @GetMapping("/admin/cs/{type}/modify/{boardId}")
    public String AdminCsFaqModify(Model model,@PathVariable int boardId,@PathVariable String type) {

        if(Objects.equals(type, "notice")) {

            List<BoardCateDTO> cate1 = boardCateService.getSubCategories(8);
            model.addAttribute("cates", cate1);
        }
        if(Objects.equals(type, "faq")) {
            List<BoardCateDTO> cate1 = boardCateService.selectBoardCatesByDepth(1);
            model.addAttribute("cate1", cate1);
        }

        BoardResponseDTO board = boardService.selectBoardById(boardId);
        log.info("수정에서 왜 카테고리가 안되지?"+ board);
        model.addAttribute("board", board);

        return "/admin/cs/"+type+"/modify";
    }

    //  faq 수정 - post
    @PostMapping("/admin/cs/{type}/modify")
    public String AdminCsFaqModify(BoardRegisterDTO boardDTO, @PathVariable String type) {

        int boardId = boardDTO.getBoardId();
        Board savedBoard = boardService.updateBoard(boardDTO);

        return "redirect:/admin/cs/"+type+"/view/"+boardId;
    }

    // 답변쓰기 - qna (get, post)
    @GetMapping("/admin/cs/qna/reply/{id}")
    public String AdminQnaReply(Model model, @PathVariable int id) {
        model.addAttribute("board", boardService.selectBoardById(id));
        return "/admin/cs/qna/reply";
    }
    @PostMapping("/admin/cs/qna/reply/{id}")
    public String AdminQnaReply(BoardCommentDTO commentDTO, @PathVariable int id) {
        commentDTO.setBoardId(id);
        boardService.insertBoardQnaComment(commentDTO);
        return "redirect:/admin/cs/qna/view/"+id;
    }
    @GetMapping("/cs/qna/subcategories/{parentId}")
    @ResponseBody
    public List<BoardCateDTO> getSubCategories(@PathVariable int parentId) {
        return boardCateService.getSubCategories(parentId);
    }
    @GetMapping("/cs/qna/categories/{boardCateId}")
    public BoardCateDTO getCategories(@PathVariable int boardCateId) {
        return boardCateService.getCategories(boardCateId);
    }



}
