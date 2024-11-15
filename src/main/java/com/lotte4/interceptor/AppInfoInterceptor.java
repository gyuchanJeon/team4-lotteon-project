package com.lotte4.interceptor;

import com.lotte4.config.AppInfo;
import com.lotte4.dto.*;
import com.lotte4.dto.admin.config.InfoDTO;
import com.lotte4.service.CategoryService;
import com.lotte4.service.ProductService;
import com.lotte4.service.VisitorService;
import com.lotte4.service.admin.config.BannerService;
import com.lotte4.service.admin.config.InfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RequiredArgsConstructor
public class AppInfoInterceptor implements HandlerInterceptor {

      /*
        Interceptor
        - 클라이언트의 요청과 컨트롤러 사이에서 특정 작업을 수행하기 위한 컴포넌트
        - HTTP 요청을 가로채고, 요청이 컨트롤러에 도달전과 후에 추가 작업 수행
        - 헤더나 푸터의 정보를 넣어줄 사용할 예정
     */

    private final AppInfo appInfo;
    private final InfoService infoService;
    private final BannerService bannerService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final VisitorService visitorService;



    //postHandle은  controller의 요청 메서드에서
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 컨트롤러 (요청 메서드)를 수행 후 실행
        if (modelAndView != null) {
            modelAndView.addObject("appInfo", appInfo);  // 기존 AppInfo 객체 추가
            InfoDTO info = infoService.selectInfoDTO();      // InfoDTO 객체 추가
            modelAndView.addObject("info", info);         // 헤더에서 사용할 info 객체 추가
            List<BannerDTO> banners = bannerService.getAllBannersWithLocation();    //모든 배너리스트 불러오기
            modelAndView.addObject("banners", banners);     // 모든 배너 추가
            List<ProductCateChildDTO> productCateDTOList = categoryService.getProductCateListWithDepth(1); //1계층 카테고리 추가
            modelAndView.addObject("productCateDTOList", productCateDTOList);
            List<ProductListDTO> SoldList = productService.getProductBest();
            modelAndView.addObject("SoldList", SoldList);
            visitorService.registerVisit(request);

        }
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //컨트롤러 를 수행 전 실행

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
