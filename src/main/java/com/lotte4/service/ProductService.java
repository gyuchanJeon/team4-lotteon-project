
package com.lotte4.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotte4.dto.*;
import com.lotte4.entity.*;
import com.lotte4.repository.*;
import com.lotte4.repository.impl.ProductRepositoryImpl;
import com.lotte4.security.MyUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/*
    수정이력

        2024-10-26
        전규찬 - formdata로 받은 String 값들을 replace / split 함수를 통해 가공하여 원하는
                데이터 타입으로 변환하는 static 메서드 생성

        - 2024/10/28 강중원 - 카테고리에 따른 상품 불러오기 기능 추가
        - 2024/10/29 강중원 - 상위 카테고리 리스트로 불러오는 기능 추가
        - 2024/10/31 전규찬 - JSON 문자열을 LinkedHashMap으로 변환하고 productDTO에 set하는 메서드 추가
                           - 파일 수정 발생 시 upload 에서 기존 파일 삭제 + 신규 파일 업로드, db에 파일명 업데이트
        - 2024/11/04 강중원 - 카테고리와 타입에 따른 정렬 기능 추가
        - 2024/11/07 전규찬 - variantId 로 productId 찾는 메서드 추가
*/

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantsRepository productVariantsRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final CachingService cachingService;
    private final SellerInfoRepository sellerInfoRepository;
    private final ProductDetailRepository detailRepository;
    private final ProductRepositoryImpl productRepositoryImpl;

    //
    public static List<String> stringToStringList(String string) {

        List<String> stringList = List.of(string.replace(" ", "").replace("[", "").replace("]", "").replace("\"", "").trim().split(","));

        return stringList;
    }

    public static List<Integer> stringToIntegerList(String string) {

        List<String> stringList = List.of(string.replace(" ", "").replace("[", "").replace("]", "").replace("\"", "").trim().split(","));
        List<Integer> intList = stringList.stream().map(Integer::parseInt).toList();

        return intList;
    }

    public static List<List<String>> stringToListInList(String string) {
        List<String> stringList = List.of(string.replace(" ", "").replace("\"[[", "").replace("]]\"", "").split("],\\["));
        List<List<String>> optionValues = new ArrayList<>();

        for (String s : stringList) {
            List<String> strings = List.of(s.split(","));
            optionValues.add(strings);
        }

        return optionValues;
    }

    @Transactional
    public ProductDTO insertProduct(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);

        // ProductDetailDTO를 ProductDetail 엔티티로 변환하고 설정
        if (productDTO.getProductDetailId() != null) {
            ProductDetail productDetail = modelMapper.map(productDTO.getProductDetailId(), ProductDetail.class);
            product.setProductDetail(productDetail);
        }
        Product savedEntity = productRepository.save(product);
        return modelMapper.map(savedEntity, ProductDTO.class);
    }

    public ProductDetailDTO insertProductDetail(ProductDetailDTO productDetailDTO) {
        ProductDetail productDetail = modelMapper.map(productDetailDTO, ProductDetail.class);
        ProductDetail savedEntity = productDetailRepository.save(productDetail);
        return modelMapper.map(savedEntity, ProductDetailDTO.class);
    }

    public List<CateForProdRegisterDTO> getProductCateByParent(int productCateId) {

        List<ProductCate> productCateList = categoryRepository.findAll();
        List<CateForProdRegisterDTO> cateForProdRegisterDTOList = new ArrayList<>();

        for (ProductCate productCate : productCateList) {
            if (productCate.getParent() != null && productCate.getParent().getProductCateId() == productCateId) {

                cateForProdRegisterDTOList.add(CateForProdRegisterDTO.builder()
                        .productCateId(productCate.getProductCateId())
                        .name(productCate.getName())
                        .depth(productCate.getDepth())
                        .build()
                );
            }
        }
        return cateForProdRegisterDTOList;
    }

    // TODO : 사용자 행동수집 type : view , uid 와 함께 mongoDB insert
    public Product_V_DTO getProduct_V_ById(int productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return new Product_V_DTO(product);
        }
        return null;
    }

    public ProductDTO getProductById(int productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return new ProductDTO(product);
        }
        return null;

    }

    public List<ProductDTO> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product : productList) {
            productDTOList.add(new ProductDTO(product));
        }
        return productDTOList;
    }

    public List<ProductListDTO> getAllProductsWithPage(int page) {
        Pageable pageable = PageRequest.of(page, 4); // 페이지당 4개의 상품을 가져옴
        return productRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(product -> modelMapper.map(product, ProductListDTO.class)) // ProductDTO로 변환
                .toList();
    }


    public List<ProductDTO> getAllProductBySellerId(int sellerId) {

        Optional<SellerInfo> optional = sellerInfoRepository.findById(sellerId);
        if (optional.isPresent()) {
            SellerInfo sellerInfo = optional.get();
            List<Product> products = productRepository.findBySellerInfoId(sellerInfo);
            List<ProductDTO> productDTOList = new ArrayList<>();
            for (Product product : products) {
                ProductDTO productDTO = new ProductDTO(product);
                productDTOList.add(productDTO);
            }
            return productDTOList;
        }

        return null;
    }


    public ProductDetailDTO getProductDetailById(int productDetailId) {
        Optional<ProductDetail> productOptional = productDetailRepository.findById(productDetailId);
        if (productOptional.isPresent()) {
            ProductDetail productDetail = productOptional.get();
            ProductDetailDTO productDetailDTO = new ProductDetailDTO(productDetail);
            log.info("productDetailDTO : " + productDetailDTO);
            return productDetailDTO;
        }
        return null;
    }

    public String uploadAndDeleteProdImg(MultipartFile file, String prevFileName) {

        String uploadDir = System.getProperty("user.dir") + "/uploads/product/";
        File fileUploadPath = new File(uploadDir);

        // 파일 업로드 디렉터리가 존재하지 않으면 디렉터리 생성
        if (!fileUploadPath.exists()) {
            fileUploadPath.mkdirs();
        }

        if (!file.isEmpty()) {
            String oName = file.getOriginalFilename();
            assert oName != null;
            String ext = oName.substring(oName.lastIndexOf("."));
            String sName = UUID.randomUUID().toString() + ext;

            // 기존 파일 삭제
            File file1 = new File(uploadDir + prevFileName);
            if (prevFileName != null) {
                try {
                    boolean deleted = file1.delete();
                } catch (Exception e) {
                    log.error(e);
                }
            }
            // 파일 저장
            try {
                file.transferTo(new File(uploadDir + "prod_img_" + sName));
            } catch (IOException e) {
                log.error(e);
            }
            return "prod_img_" + sName;
        }
        return null;
    }


    @Transactional
    public void makeVariantDTOAndInsert(String optionNames, String prodONames, String prodPrices, String prodStocks, String mixedValuesList, String productId) {

        try {
            // 각 string 들을 알맞은 list 나 map 형식으로 바꿔주기
            List<String> option_names = stringToStringList(optionNames);
            List<String> prod_oNames = stringToStringList(prodONames);
            List<Integer> prod_prices = stringToIntegerList(prodPrices);
            List<Integer> prod_stocks = stringToIntegerList(prodStocks);
            List<List<String>> mixed_values_list = stringToListInList(mixedValuesList);
            int prodId = Integer.parseInt(productId.replace("\"", ""));

            // 리스트 크기 검증
            int size = prod_oNames.size();
            if (prod_prices.size() != size || prod_stocks.size() != size || mixed_values_list.size() != size) {
                throw new IllegalArgumentException("모든 리스트의 크기가 동일해야 합니다.");
            }

            // 동일한 인덱스의 값을 동시에 가져오는 반복문
            for (int i = 0; i < prod_oNames.size(); i++) {

                ProductVariantsDTO productVariantsDTO = new ProductVariantsDTO();
                Map<List<String>, List<String>> options = new LinkedHashMap<>();

                String sku = prod_oNames.get(i);
                int price = prod_prices.get(i);
                int stock = prod_stocks.get(i);
                options.put(option_names, mixed_values_list.get(i));

                productVariantsDTO.setSku(sku);
                productVariantsDTO.setPrice(price);
                productVariantsDTO.setStock(stock);
                productVariantsDTO.setOptions(options);
                productVariantsDTO.setProduct(getProductById(prodId));

                productVariantsRepository.save(modelMapper.map(productVariantsDTO, ProductVariants.class));
            }
        } catch (Exception e) {
            log.error(e);
        }

    }

    @Transactional
    public void makeVariantDTOAndUpdate(String prodONames, String prodPrices, String prodStocks, String variantsIds, String valuesList, String optionNames, String productId) {

        try {
            // 각 string 들을 알맞은 list 나 map 형식으로 바꿔주기
            List<String> prod_oNames = stringToStringList(prodONames);
            List<Integer> prod_prices = stringToIntegerList(prodPrices);
            List<Integer> prod_stocks = stringToIntegerList(prodStocks);
            List<Integer> variants_ids = stringToIntegerList(variantsIds);
            List<String> option_names = stringToStringList(optionNames);
            int prodId = Integer.parseInt(productId.replace("\"", ""));

            // valueList  형식 변환
            List<String> stringList = List.of(valuesList.replace("\"", "").replace("[[", "").replace("]]", "").split("],\\["));
            List<List<String>> values_list = new ArrayList<>();

            for (String s : stringList) {
                List<String> strings = List.of(s.split(","));
                values_list.add(strings);
            }

            log.info("values_list = " + values_list);

            // 리스트 크기 검증
            int size = prod_oNames.size();
            if (prod_prices.size() != size || prod_stocks.size() != size || variants_ids.size() != size || values_list.size() != size) {
                throw new IllegalArgumentException("모든 리스트의 크기가 동일해야 합니다.");
            }

            // 동일한 인덱스의 값을 동시에 가져오는 반복문
            for (int i = 0; i < prod_oNames.size(); i++) {

                ProductVariantsDTO productVariantsDTO = new ProductVariantsDTO();
                Map<List<String>, List<String>> options = new LinkedHashMap<>();

                String sku = prod_oNames.get(i);
                int price = prod_prices.get(i);
                int stock = prod_stocks.get(i);
                int variantId = variants_ids.get(i);
                options.put(option_names, values_list.get(i));

                productVariantsDTO.setSku(sku);
                productVariantsDTO.setPrice(price);
                productVariantsDTO.setStock(stock);
                productVariantsDTO.setVariant_id(variantId);
                productVariantsDTO.setOptions(options);
                productVariantsDTO.setProduct(getProductById(prodId));

                log.info("productVariantsDTO : " + productVariantsDTO);

                productVariantsRepository.save(modelMapper.map(productVariantsDTO, ProductVariants.class));
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Transactional
    public String deleteProductAndImagesById(int productId) {
        String uploadDir = System.getProperty("user.dir") + "/uploads/product/";

        Optional<Product> optProd = productRepository.findById(productId);
        if (optProd.isPresent()) {
            Product product = optProd.get();
            String img1 = product.getImg1();
            String img2 = product.getImg2();
            String img3 = product.getImg3();
            String detail = product.getDetail();

            // 기존 파일 삭제
            File file1 = new File(uploadDir + img1);
            File file2 = new File(uploadDir + img2);
            File file3 = new File(uploadDir + img3);
            File file4 = new File(uploadDir + detail);

            try {
                file1.delete();
                file2.delete();
                file3.delete();
                file4.delete();
            } catch (Exception e) {
                log.error(e);
            }
        }
        productRepository.deleteById(productId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            return "failure";
        }
        return "success";
    }

    public List<ProductCateDTO> getProductCates(int cate) {
        List<ProductCateDTO> productCateDTOList = new ArrayList<>();
        Optional<ProductCate> productCateOpt = categoryRepository.findById(cate);
        if (productCateOpt.isPresent()) {
            ProductCate productCate = productCateOpt.get();
            productCateDTOList.add(modelMapper.map(productCate, ProductCateDTO.class));
            while (productCate.getParent() != null) {
                productCate = productCate.getParent();
                productCateDTOList.add(modelMapper.map(productCate, ProductCateDTO.class));
            }
        }
        Collections.reverse(productCateDTOList);
        return productCateDTOList;
    }

    public List<ProductDTO> getProductWithCate(int cate) {
        List<ProductDTO> productDTOList = new ArrayList<>();
        Optional<ProductCate> productCate = categoryRepository.findById(cate);


        log.info(productDTOList.toString());


        if (productCate.isPresent()) {
            // depth가 1 또는 2인 경우, 하위 카테고리의 제품을 가져옴
            if (productCate.get().getChildren() != null) {
                for (ProductCate child : productCate.get().getChildren()) {
                    // 현재 하위 카테고리에 속한 제품을 가져옴
                    List<Product> childProducts = productRepository.findByProductCateId(child);
                    for (Product product : childProducts) {
                        productDTOList.add(new ProductDTO(product));
                    }

                    // depth가 1인 경우, 손자 카테고리의 제품을 가져옴
                    if (child.getChildren() != null) {
                        for (ProductCate grandChild : child.getChildren()) {
                            List<Product> grandChildProducts = productRepository.findByProductCateId(grandChild);
                            for (Product product : grandChildProducts) {
                                productDTOList.add(new ProductDTO(product));
                            }
                        }
                    }
                }
            }

            // 해당 카테고리에 속한 직접적인 제품도 가져옴
            List<Product> products = productRepository.findByProductCateId(productCate.get());
            for (Product product : products) {
                productDTOList.add(new ProductDTO(product));
            }
        }
        log.info(productDTOList.toString());
        return productDTOList;
    }

    //2024.11.04 - 강중원 - 리스트별 정렬 알고리즘
    public List<ProductDTO> getProductWithCateAndType(int cate, String type) {
        List<ProductDTO> productDTOList = new ArrayList<>();
        //home(전체상품)
        if (cate == 0) {
            productDTOList = getAllProducts();
        }
        //카테고리가 있을경우
        else {
            productDTOList = getProductWithCate(cate);
        }


        //정렬
        switch (type) {
            //낮은 가격
            case "lowPrice":
                productDTOList.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {

                        double o1Price = o1.getPrice() * (1 - o1.getDiscount() / 100.0);
                        double o2Price = o2.getPrice() * (1 - o2.getDiscount() / 100.0);

                        if (o1Price > o2Price) {
                            return 1;
                        } else if (o1Price < o2Price) {
                            return -1;
                        }
                        return 0;
                    }
                });
                break;

            //높은 가격순
            case "highPrice":
                productDTOList.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        double o1Price = o1.getPrice() * (1 - o1.getDiscount() / 100.0);
                        double o2Price = o2.getPrice() * (1 - o2.getDiscount() / 100.0);

                        if (o1Price > o2Price) {
                            return -1;
                        } else if (o1Price < o2Price) {
                            return 1;
                        }
                        return 0;
                    }
                });
                break;

            //판매량
            case "sold":
                productDTOList.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return Integer.compare(o2.getSold(), o1.getSold());
                    }
                });
                break;

            //평점 높은순
            case "highReview":
                productDTOList.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return Double.compare(o2.getRating(), o1.getRating());
                    }
                });
                break;

            //리뷰 많은순
            case "manyReview":
                productDTOList.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return Double.compare(o2.getReview(), o1.getReview());
                    }
                });
                break;

            //최근 등록순
            case "recent":
                productDTOList.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                    }
                });
                break;
        }
        return productDTOList;
    }

    //home(index)에서 사용
    public List<ProductListDTO> getProductWithType(String type) {
        List<ProductListDTO> productDTOList = new ArrayList<>();

        List<Product> products = new ArrayList<>();
        //정렬
        switch (type) {
            case "Hit":
                products = productRepository.findTop8ByOrderByHitDesc();
                for (Product product : products) {
                    productDTOList.add(modelMapper.map(product, ProductListDTO.class));
                }
                break;
            case "Score":
                products = productRepository.findTop8ByOrderByRatingDesc();
                for (Product product : products) {
                    productDTOList.add(modelMapper.map(product, ProductListDTO.class));
                }
                break;
            case "ScoreMany":
                products = productRepository.findTop8ByOrderByReviewDesc();
                for (Product product : products) {
                    productDTOList.add(modelMapper.map(product, ProductListDTO.class));
                }
                break;

            case "Discount":
                products = productRepository.findTop8ByOrderByDiscountDesc();
                for (Product product : products) {
                    productDTOList.add(modelMapper.map(product, ProductListDTO.class));
                }
                break;
            case "Recent":
                products = productRepository.findTop8ByOrderByCreatedAtDesc();
                for (Product product : products) {
                    productDTOList.add(modelMapper.map(product, ProductListDTO.class));
                }
                break;
        }
        return productDTOList;
    }

    public List<ProductListDTO> getProductBest() {
        return cachingService.getProductBest();
    }


    public ProductDTO JsonToMapAndSetProductDTO(String optionsJson, ProductDTO productDTO) {

        // 'options' JSON 문자열을 LinkedHashMap<String, List<String>>으로 변환
        LinkedHashMap<String, List<String>> optionsMap = null;
        try {
            optionsMap = objectMapper.readValue(optionsJson,
                    new TypeReference<LinkedHashMap<String, List<String>>>() {
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        productDTO.setOptions(optionsMap);
        return productDTO;
    }

    public ProductDTO updateProdImg(MultipartFile img1, MultipartFile img2, MultipartFile img3, MultipartFile detail, ProductDTO productDTO, Product_V_DTO product_V_DTO) {

        if (img1 != null) {
            String prevImg = productDTO.getImg1();
            String newImg1 = uploadAndDeleteProdImg(img1, prevImg);
            productDTO.setImg1(newImg1);
        } else {
            productDTO.setImg1(product_V_DTO.getImg1());
        }
        if (img2 != null) {
            String prevImg = productDTO.getImg2();
            String newImg2 = uploadAndDeleteProdImg(img2, prevImg);
            productDTO.setImg2(newImg2);
        } else {
            productDTO.setImg2(product_V_DTO.getImg2());
        }
        if (img3 != null) {
            String prevImg = productDTO.getImg3();
            String newImg3 = uploadAndDeleteProdImg(img3, prevImg);
            productDTO.setImg3(newImg3);
        } else {
            productDTO.setImg3(product_V_DTO.getImg3());
        }
        if (detail != null) {
            String prevImg = productDTO.getDetail();
            String newDetail = uploadAndDeleteProdImg(detail, prevImg);
            productDTO.setDetail(newDetail);
        } else {
            productDTO.setDetail(product_V_DTO.getDetail());
        }
        return productDTO;
    }

    public List<ProductDTO> getProductListWithKeyword(String keyword, List<String> filters, int minPrice, int maxPrice) {
        List<ProductDTO> productDTOList = new ArrayList<>();

        List<Product> products = productRepositoryImpl.findByKeywordWithFilters(keyword, filters, minPrice, maxPrice);
        for (Product product : products) {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }


    // role 에 해당하는 상품 목록 select
    public Page<ProductDTO> selectProductListByRole(int page, int size, String keyword, String searchCategory, MyUserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = null;
        String role = userDetails.getUser().getRole();

        if ((keyword == null || keyword.isEmpty()) && role.equals("ADMIN")) {
            productPage = productRepository.findAll(pageable);
            // product 엔티티를 ProductDTO 변환
            return productPage.map(ProductDTO::new);
        } else if ((keyword == null || keyword.isEmpty()) && role.equals("seller")) {
            SellerInfo sellerInfoId = userDetails.getUser().getSellerInfo();
            productPage = productRepository.findBySellerInfoId(sellerInfoId, pageable);
            // product 엔티티를 ProductDTO 변환
            assert productPage != null;
            return productPage.map(ProductDTO::new);
        }


        // 검색 키워드가 있을 때 searchCategory에 따라 조건을 나눔
        if (keyword != null && !keyword.isEmpty() && role.equals("seller")) {
            SellerInfo sellerInfoId = userDetails.getUser().getSellerInfo();
            switch (searchCategory) {
                case "name":
                    productPage = productRepository.findBySellerInfoIdAndNameContaining(sellerInfoId, keyword, pageable);
                    break;
                case "productId":
                    int intKeyword = Integer.parseInt(keyword);
                    productPage = productRepository.findBySellerInfoIdAndProductId(sellerInfoId, intKeyword, pageable);
                    break;
                case "company":
                    productPage = productRepository.findBySellerInfoIdAndCompanyContaining(sellerInfoId, keyword, pageable);
                    break;
                default:
                    // 기본적으로 모든 필드를 포함하는 검색
                    productPage = productRepository.findBySellerInfoId(
                            sellerInfoId, pageable);
                    break;
            }
        } else if (keyword != null && !keyword.isEmpty() && role.equals("ADMIN")) {
            switch (searchCategory) {
                case "name":
                    productPage = productRepository.findByName(keyword, pageable);
                    break;
                case "productId":
                    int intKeyword = Integer.parseInt(keyword);
                    productPage = productRepository.findByProductId(intKeyword, pageable);
                    break;
                case "sellerInfoId":
                    int intKeyword1 = Integer.parseInt(keyword);
                    Optional<SellerInfo> optional = sellerInfoRepository.findById(intKeyword1);
                    if (optional.isPresent()) {
                        SellerInfo sellerInfo = optional.get();
                        productPage = productRepository.findBySellerInfoId(sellerInfo, pageable);
                    }
                    break;
                case "company":
                    productPage = productRepository.findByCompany(keyword, pageable);
                    break;
                default:
                    // 기본적으로 모든 필드를 포함하는 검색
                    productPage = productRepository.findAll(pageable);
                    break;
            }
        }
        // product 엔티티를 ProductDTO 변환
        assert productPage != null;
        return productPage.map(ProductDTO::new);
    }

    public List<ProductDTO> orderProductList(List<ProductDTO> productDTOListPre, String type) {

        //정렬
        switch (type) {
            //낮은 가격
            case "lowPrice":
                productDTOListPre.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {

                        double o1Price = o1.getPrice() * (1 - o1.getDiscount() / 100.0);
                        double o2Price = o2.getPrice() * (1 - o2.getDiscount() / 100.0);

                        if (o1Price > o2Price) {
                            return 1;
                        } else if (o1Price < o2Price) {
                            return -1;
                        }
                        return 0;
                    }
                });
                break;

            //높은 가격순
            case "highPrice":
                productDTOListPre.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        double o1Price = o1.getPrice() * (1 - o1.getDiscount() / 100.0);
                        double o2Price = o2.getPrice() * (1 - o2.getDiscount() / 100.0);

                        if (o1Price > o2Price) {
                            return -1;
                        } else if (o1Price < o2Price) {
                            return 1;
                        }
                        return 0;
                    }
                });
                break;

            //판매량
            case "sold":
                productDTOListPre.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return Integer.compare(o2.getSold(), o1.getSold());
                    }
                });
                break;
            //평점 높은순
            case "highReview":
                productDTOListPre.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return Double.compare(o2.getRating(), o1.getRating());
                    }
                });
                break;

            //리뷰 많은순
            case "manyReview":
                productDTOListPre.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return Double.compare(o2.getReview(), o1.getReview());
                    }
                });
                break;

            //최근 등록순
            case "recent":
                productDTOListPre.sort(new Comparator<ProductDTO>() {
                    @Override
                    public int compare(ProductDTO o1, ProductDTO o2) {
                        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                    }
                });
                break;
        }
        return productDTOListPre;
    }

    public int getProductIdByVariantId(int variantId) {

        // variant_id로 ProductVariants 조회
        ProductVariants productVariant = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid variant ID: " + variantId));

        return productVariant.getProduct().getProductId();
    }


    public ProductVariants findProductVariantById(int id) {
        Optional<ProductVariants> optionalProductVariant = productVariantsRepository.findById(id);

        if (optionalProductVariant.isPresent()) {
            return optionalProductVariant.get();
        } else {
            // 값이 없을 때 처리, 예외 던지기 또는 null 반환
            throw new NoSuchElementException("Product variant with ID " + id + " not found");
        }
    }
}