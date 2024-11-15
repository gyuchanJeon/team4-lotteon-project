package com.lotte4.service;

import com.lotte4.dto.*;
import com.lotte4.dto.admin.config.InfoDTO;
import com.lotte4.entity.Banner;
import com.lotte4.entity.Info;
import com.lotte4.entity.Product;
import com.lotte4.entity.ProductCate;
import com.lotte4.repository.CategoryRepository;
import com.lotte4.repository.ProductRepository;
import com.lotte4.repository.admin.config.BannerRepository;
import com.lotte4.repository.admin.config.InfoRepository;
import com.lotte4.service.admin.config.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*

    배너 이미지 파일 삭제 추가 11.01 강중원

 */


@Log4j2
@Service
@RequiredArgsConstructor
public class CachingService {

    private final ModelMapper modelMapper;
    private final BannerRepository bannerRepository;
    private final CategoryRepository categoryRepository;
    private final InfoRepository infoRepository;
    private final ProductRepository productRepository;



    //배너 캐싱
    @Cacheable(key = "'allBannersWithLocation'", value = "banners")
    public List<List<BannerDTO>> getAllBannersWithLocationPre(){
        List<List<Banner>> banners = new ArrayList<>();

        // 구역별 리스트 전부 불러오기
        String[] bannerLocationList = {"MAIN2","MAIN1","PRODUCT1","MY1","MEMBER1"};
        for(String location : bannerLocationList){
            List<Banner> bannerTMP = bannerRepository.findByLocationAndState(location,1);
            if (!bannerTMP.isEmpty()){
                banners.add(bannerTMP);
            }
            else{
                //활성화된 배너가 없다면
                Banner banner = new Banner();
                banner.setLocation(location);
                banner.setState(1);
                banner.setImg("default_"+location+".png");
                banner.setName("default_"+location+"_Banner");
                banner.setBackground("#FFFFFF");
                banner.setLink("http://13.125.226.80:8080/lotteon/index");
                List<Banner> bannerList = new ArrayList<>();
                bannerList.add(banner);

                banners.add(bannerList);
            }
        }
        List<List<BannerDTO>> bannerDTOs = new ArrayList<>();

        for(List<Banner> banner : banners){
            List<BannerDTO> bannerDTO = new ArrayList<>();
            for(Banner obj :banner){
                BannerDTO objDTO = modelMapper.map(obj, BannerDTO.class);
                bannerDTO.add(objDTO);
            }
            bannerDTOs.add(bannerDTO);
        }
        return bannerDTOs;
    }

    @Cacheable(key = "'allBanners'", value = "allBanners")
    public List<BannerDTO> getAllBanners(){
        List<Banner> banners = bannerRepository.findAll();
        List<BannerDTO> bannerDTOs = new ArrayList<>();

        for(Banner banner : banners){
                bannerDTOs.add(modelMapper.map(banner, BannerDTO.class));
        }
        return bannerDTOs;
    }


    @CacheEvict(value = "banners", key = "'allBannersWithLocation'")
    public void updateBannerState(BannerDTO bannerDTO, int state){
        Optional<Banner> bannerOpt = bannerRepository.findById(bannerDTO.getBannerId());
        if(bannerOpt.isPresent()){
            Banner banner = bannerOpt.get();
            banner.setState(state);
            bannerRepository.save(banner);
        }
    }


    @CacheEvict(value = "banners", key = "'allBannersWithLocation'")
    public BannerDTO insertBanner(BannerDTO bannerDTO, MultipartFile bannerImg) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/uploads/config/";

        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs()) {
                throw new IOException("디렉토리를 생성할 수 없습니다: " + uploadDir);
            }
        }

        if (!bannerImg.isEmpty()) {
            String bannerImgFileName = "banner_" + bannerDTO.getName() + "_" + bannerDTO.getLocation() + "_" + System.currentTimeMillis() + ".png";
            bannerImg.transferTo(new File(uploadDir + bannerImgFileName));
            bannerDTO.setImg(bannerImgFileName);
        }


        Banner banner = modelMapper.map(bannerDTO, Banner.class);
        banner.setState(1);
        Banner resultBanner = bannerRepository.save(banner);

        return modelMapper.map(resultBanner, BannerDTO.class);
    }


    @CacheEvict(value = "banners", key = "'allBannersWithLocation'")
    public void deleteBanner(int bannerId){
        //이미지삭제 - 11.01
        Optional<Banner> banner = bannerRepository.findById(bannerId);
        String FileName = "";

        bannerRepository.deleteById(bannerId);

        if(banner.isPresent()){
            FileName = banner.get().getImg();
            String uploadDir = System.getProperty("user.dir") + "/uploads/config/";

            File file1 = new File(uploadDir + FileName);
            if (FileName != null) {
                try {
                    boolean deleted = file1.delete();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    @CacheEvict(value = "banners", key = "'allBannersWithLocation'")
    public void clearAllBannersWithLocation(){
    }
    @CacheEvict(key = "'allBanners'", value = "allBanners")
    public void clearAllEnableBanners(){
    }



    //카테고리
    @Cacheable(key = "'getProductCateListWithDepth'", value = "categories")
    public List<ProductCateChildDTO> getProductCateListWithDepth(int depth){
        List<ProductCate> productCateList = categoryRepository.findByDepth(depth);
        //인덱스별로 정렬
        List<ProductCate> productCateOrderByIndex = OrderCate(productCateList);

        return productCateOrderByIndex.stream().map(ProductCateChildDTO::new).collect(Collectors.toList());
    }

    public List<ProductCate> OrderCate(List<ProductCate> productCateList) {

        for(ProductCate productCate : productCateList){
            //만일 자식리스트가 있다면
            if(!productCate.getChildren().isEmpty()){
                //자식 리스트 먼저 정렬
                List<ProductCate> OrderedChild = OrderCate(productCate.getChildren());
                productCate.setChildren(OrderedChild);
            }
        }
        //자식이 없거나 자식 정렬이 끝났으면
        productCateList.sort(new Comparator<ProductCate>() {
            @Override
            public int compare(ProductCate o1, ProductCate o2) {
                return Integer.compare(o1.getCateIndex(), o2.getCateIndex());
            }
        });
        return productCateList;
    }

    // 삭제
    @CacheEvict(key = "'getProductCateListWithDepth'", value = "categories")
    public boolean deleteProductCate(String name){

        ProductCate productCate = categoryRepository.findByName(name);
        if(productCate != null){
            categoryRepository.delete(productCate);
            return true;
        }
        else {
            return false;
        }

    }

    //삽입
    @CacheEvict(key = "'getProductCateListWithDepth'", value = "categories")
    public void insertProductCate(ProductRegisterCateDTO productCateDTO, String parent){

        ProductCate productCate = modelMapper.map(productCateDTO, ProductCate.class);

        //depth가 1보다 큰경우
        //--> 제일 상위 카테고리가 아닌경우
        if(productCate.getDepth() > 1){
            ProductCate parentCate = categoryRepository.findByName(parent);
            productCate.setCateIndex(parentCate.getChildren().size());
            productCate.setParent(parentCate);
        }
        else{
            productCate.setCateIndex(categoryRepository.findByDepth(1).size());
        }
        categoryRepository.save(productCate);
    }

    @CacheEvict(key = "'getProductCateListWithDepth'", value = "categories")
    public boolean updateProductCateOrder(List<Map<String, Object>> changes){
        try {
            for (Map<String, Object> change : changes) {
                log.info("change: "+change);
                log.info("name: "+change.get("name"));
                ProductCate productCate = categoryRepository.findByName(change.get("name").toString());
                productCate.setCateIndex(Integer.parseInt(change.get("order").toString()));
                categoryRepository.save(productCate);
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }


    //헤더(Info)
    @Cacheable(key = "'selectInfoDTO'", value = "Info") //캐싱 기능추가 10.31 강중원
    public InfoDTO selectInfoDTO() {
        return infoRepository.findById(1)
                .map(info -> modelMapper.map(info, InfoDTO.class))
                .orElse(null);
    }

    @Transactional
    @CacheEvict(key = "'selectInfoDTO'", value = "Info")
    public InfoDTO updateInfoTitleAndSubtitle(InfoDTO infoDTO) {
        int resultRow = infoRepository.updateTitleAndSubtitle(infoDTO.getTitle(), infoDTO.getSubTitle());
        // 이 값이 1일 때 성공적으로 수정된 것을 의미
        if(resultRow ==1){
            return infoDTO;
        }else
            return null;
    }

    @Transactional
    @CacheEvict(key = "'selectInfoDTO'", value = "Info")
    public InfoDTO updateCompanyInfo(InfoDTO infoDto) {
        int resultRow = infoRepository.updateCompanyInfo(
                infoDto.getCompanyName(),
                infoDto.getCompanyCeo(),
                infoDto.getCompanyBusinessNumber(),
                infoDto.getMosaNumber(),
                infoDto.getCompanyAddress(),
                infoDto.getCompanyAddress2()
        );
        if(resultRow ==1){
            return infoDto;
        }else
            return null;
    }

    @Transactional
    @CacheEvict(key = "'selectInfoDTO'", value = "Info")
    public InfoDTO updateCompanyCs(InfoDTO infoDto) {
        int resultRow = infoRepository.updateCompanyCs(
                infoDto.getCsHp(),
                infoDto.getCsWorkingHours(),
                infoDto.getCsEmail(),
                infoDto.getConsumer()
        );
        if(resultRow ==1){
            return infoDto;
        }else
            return null;
    }

    @Transactional
    @CacheEvict(key = "'selectInfoDTO'", value = "Info")
    public InfoDTO updateCopyright(InfoDTO infoDto) {
        int resultRow = infoRepository.updateCopyright(
                infoDto.getCopyright()
        );
        if(resultRow ==1){
            return infoDto;
        }else
            return null;
    }


    @CacheEvict(key = "'selectInfoDTO'", value = "Info")
    public InfoDTO uploadLogos(MultipartFile headerLogo, MultipartFile footerLogo, MultipartFile favicon) throws IOException {
        Info info = infoRepository.findById(1).orElseThrow();
        String uploadDir = System.getProperty("user.dir") + "/uploads/config/";

        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs()) {
                throw new IOException("디렉토리를 생성할 수 없습니다: " + uploadDir);
            }
        }

        if (!headerLogo.isEmpty()) {
            String headerLogoFileName = "headerLogo_" + System.currentTimeMillis() + ".png";
            headerLogo.transferTo(new File(uploadDir + headerLogoFileName));
            info.setHeaderLogo(headerLogoFileName);
        }

        if (!footerLogo.isEmpty()) {
            String footerLogoFileName = "footerLogo_" + System.currentTimeMillis() + ".png";
            footerLogo.transferTo(new File(uploadDir + footerLogoFileName));
            info.setFooterLogo(footerLogoFileName);
        }

        if (!favicon.isEmpty()) {
            String faviconFileName = "favicon_" + System.currentTimeMillis() + ".ico";
            favicon.transferTo(new File(uploadDir + faviconFileName));
            info.setFavicon(faviconFileName); // 파일명 저장
        }

        // Info 엔티티 저장
        return modelMapper.map(infoRepository.save(info), InfoDTO.class);
    }

    @Cacheable(key = "'BestProductDTO'", value = "BestProduct")
    public List<ProductListDTO> getProductBest(){
        List<ProductListDTO> productDTOList = new ArrayList<>();
        List<Product> products = productRepository.findTop5ByOrderBySoldDesc();
        for(Product product : products){
            productDTOList.add(modelMapper.map(product, ProductListDTO.class));
        }
        return productDTOList;
    }

}
