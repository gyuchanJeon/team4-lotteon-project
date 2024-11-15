package com.lotte4.controller.pagecontroller;

import com.lotte4.dto.ProductBestDTO;
import com.lotte4.service.BestProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@RequiredArgsConstructor
@Controller
public class ProductBestController {

    private final BestProductService bestProductService;

    // 클라이언트 연결을 관리할 리스트 (복수 클라이언트 지원)
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // 이전 상위 5개 제품 리스트를 저장하는 클래스 레벨 필드
    private List<ProductBestDTO> previousTop5 = new ArrayList<>();


    @PostMapping("/product/soldUpdate/{sold}")
    @ResponseBody
    public void updateBest(@RequestBody ProductBestDTO soldItem, @PathVariable int sold) {
        bestProductService.updateSalesInRedis(soldItem, sold);
    }

    // 클라이언트가 SSE에 연결을 요청하면 이를 처리
    @GetMapping(value = "/sse/top5", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter subscribeToTop5() {
        SseEmitter emitter = new SseEmitter();
//        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);  // 연결된 클라이언트를 리스트에 추가

        // 연결이 끝나면 emitter를 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        try {
            // 초기 상위 5개 제품 리스트 전송
            List<ProductBestDTO> top5Products = bestProductService.getTop5BestSelling();
            emitter.send(top5Products);  // 데이터를 보내고 연결을 열어둠

            // 데이터를 계속 보낼 수 있도록 연결 유지
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }


    // 상위 5개 제품 정보가 변경될 때마다 클라이언트에게 전송
    public void sendUpdateToClients(List<ProductBestDTO> currentTop5) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("top5").data(currentTop5));
                log.info("emitter sent to top5");
            } catch (Exception e) {
                emitters.remove(emitter);
                log.error("Error sending update to client, removing emitter: {}", emitter, e);
            }
        }
    }


    // 트리거 발생 시 상위 5개가 변경되면 SSE 전송
    @Scheduled(fixedRate = 10000) //10초
    public void checkAndNotifyTop5Change() {
        List<ProductBestDTO> currentTop5 = bestProductService.getTop5BestSelling();

        // 순서만 비교
        if (!isSameOrder(currentTop5, previousTop5)) {
            log.info("currentTop5: {}", currentTop5);
            log.info("previousTop5: {}", previousTop5);

            previousTop5 = new ArrayList<>(currentTop5); // 깊은 복사하여 갱신

            sendUpdateToClients(currentTop5); // SSE로 클라이언트에 전달
        } else {
//            log.info("not Changed!!");
        }
    }


    private boolean isSameOrder(List<ProductBestDTO> list1, List<ProductBestDTO> list2) {
        // 리스트 크기가 다르면 순서가 다르다고 판단
        if (list1.size() != list2.size()) {
            return false;
        }

        // 순서대로 비교
        for (int i = 0; i < list1.size(); i++) {
            // productId로 비교하여 순서를 체크 (기타 필드도 필요시 비교)
            if (list1.get(i).getProductId() != list2.get(i).getProductId()) {
                return false;
            }
        }
        return true;  // 순서가 같으면 true
    }


}
