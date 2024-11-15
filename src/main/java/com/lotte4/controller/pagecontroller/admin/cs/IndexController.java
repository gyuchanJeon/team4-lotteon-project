package com.lotte4.controller.pagecontroller.admin.cs;

import com.lotte4.entity.Board;
import com.lotte4.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class IndexController {

    public final BoardService boardService;

    @GetMapping("/cs/index")
    public String showNoticeList(Model model) {
        List<Board> noticeBoards = boardService.findTop5ByOrderByRegdateDesc("notice");
        List<Board> qnaBoards = boardService.findTop5ByOrderByRegdateDesc("qna").stream()
                .map(board -> {
                  String maskedId = maskId(board.getUser().getUid());
                  board.setMaskedUserId(maskedId);
                  return board;
                })
                        .collect(Collectors.toList());
        model.addAttribute("noticeBoards", noticeBoards);
        model.addAttribute("qnaBoards", qnaBoards);
        return "/cs/index";
    }


    private String maskId(String userId) {
        if (userId.length() < 3) {
            return userId + "***";
        }
        String visiblePart = userId.substring(0, 3);
        return visiblePart + "***";
    }





}
