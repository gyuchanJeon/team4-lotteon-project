package com.lotte4.controller.pagecontroller.admin.cs;

import com.lotte4.service.board.BoardCateService;
import com.lotte4.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class NoticeController {

    private final BoardCateService boardCateService;
    private final BoardService boardService;



}
