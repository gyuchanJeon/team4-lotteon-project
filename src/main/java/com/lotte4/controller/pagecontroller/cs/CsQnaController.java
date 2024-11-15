package com.lotte4.controller.pagecontroller.cs;

import com.lotte4.dto.BoardCateDTO;
import com.lotte4.dto.BoardRegisterDTO;
import com.lotte4.dto.BoardResponseDTO;
import com.lotte4.entity.Board;
import com.lotte4.service.UserService;
import com.lotte4.service.board.BoardCateService;
import com.lotte4.service.board.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
/*
     이름 : 황수빈(최초 작성자)
     내용 : csQnaController 생성

     수정이력
      - 2024/10/29 황수빈 - 카테고리별로 다른 게시물 뿌리기 하는 중
      - 2024/11/06 전규찬 - 채용하기 페이지 출력
*/
@Log4j2
@RequiredArgsConstructor
@Controller
public class  CsQnaController {

    private final UserService userService;
    private final BoardCateService boardCateService;
    private final BoardService boardService;

    // qna 글쓰기
    @GetMapping("/cs/qna/write")
    public String qnaWrite(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/member/login";
        }

        List<BoardCateDTO> cate1 = boardCateService.selectBoardCatesByDepth(1);
        model.addAttribute("cate1", cate1);
        return "/cs/qna/write";
    }


    @PostMapping("/cs/{type}/write")
    public String qnaWrite(BoardRegisterDTO dto, HttpServletRequest req, @PathVariable String type) {

        dto.setRegIp(req.getRemoteAddr());
        Board savedBoard = boardService.insertBoard(dto);
        if(savedBoard!=null){
        if(Objects.equals(type, "qna")){
            return "redirect:/cs/qna/list";
        }
        if(Objects.equals(type, "faq")){
            return "redirect:/admin/cs/faq/list";
        }
        if(Objects.equals(type, "notice")){
            return "redirect:/admin/cs/notice/list";
        }
        }
        return null;

    }

    //공지사항 작성
    @GetMapping("/admin/cs/notice/write")
    public String AdminNoticeWrite(Model model) {
        model.addAttribute("cates",boardCateService.getSubCategories(8));
        return "/admin/cs/notice/write";
    }

    // cs
    @GetMapping({"/cs/{type}/list", "/cs/{type}/list/{cate}"})
    public String qna(Model model,
                      @PathVariable String type,
                      Principal principal,
                      @PathVariable(required = false) Integer cate,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "8") int size) {

        // QnA 타입의 경우 로그인 필수 확인
        if ("qna".equals(type) && principal == null) {
            return "redirect:/member/login";
        }
        // 사용자 ID 설정
        String uid = (principal != null) ? principal.getName() : null;
        log.info("uid뭐라고 들어옴 ? "+uid);
        Page<BoardResponseDTO> boardList;
        if(type.equals("qna")){
            boardList = boardService.selectArticleByQnaAndUid(type, uid, page, size);
            log.info("qna일때"+boardList);
        }else {
            boardList = (cate == null)
                    ? boardService.selectAllBoardByType(type, page, size)
                    : boardService.selectAllBoardByCateId(cate, type, page, size);
        }
        model.addAttribute("boards", boardList.getContent());
        model.addAttribute("totalPages", boardList.getTotalPages());
        model.addAttribute("currentPage", page);

        return "/cs/" + type + "/list";
    }




    // 글보기 : qna, faq
    @GetMapping("/cs/{type}/view/{id}")
    public String qnaView(Model model, @PathVariable String type, @PathVariable int id) {
        model.addAttribute("board", boardService.selectBoardById(id));
        log.info("어떻게 오나 함 보자"+boardService.selectBoardById(id));
        return "/cs/"+type+"/view";
    }


}

