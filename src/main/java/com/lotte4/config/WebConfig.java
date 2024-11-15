package com.lotte4.config;

import com.lotte4.interceptor.AppInfoInterceptor;
import com.lotte4.service.CategoryService;
import com.lotte4.service.ProductService;
import com.lotte4.service.VisitorService;
import com.lotte4.service.admin.config.BannerService;
import com.lotte4.service.admin.config.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AppInfo appInfo;

    @Autowired
    private InfoService infoService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private VisitorService visitorService;

    // 업로드된 이미지를 html에서 가져오기 위해서
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AppInfoInterceptor(appInfo, infoService, bannerService, categoryService,productService,visitorService));

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/sse/**") // SSE URL 패턴에 대해 CORS 설정
                .allowedOrigins("http://13.125.226.80:8080","http://localhost:8080") // 클라이언트 도메인
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
