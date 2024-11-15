package com.lotte4.controller.pagecontroller;

import com.lotte4.dto.ProductCateDTO;
import com.lotte4.dto.ProductDTO;
import com.lotte4.dto.ProductListDTO;
import com.lotte4.service.CategoryService;
import com.lotte4.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/*

    2024.11.04 - 강중원 - 메인화면에 삽입되는 리스트 추가

 */

@Log4j2
@Controller
@AllArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "/index";
    }

    @GetMapping("/index/api/{location}")
    @ResponseBody
    public List<ProductListDTO> getHomeList(@PathVariable String location) {
        return productService.getProductWithType(location);
    }

    @GetMapping("/index/api/all-products")
    @ResponseBody
    public List<ProductListDTO> getAllList(@RequestParam("page") int page) {
        return productService.getAllProductsWithPage(page);
    }


}
