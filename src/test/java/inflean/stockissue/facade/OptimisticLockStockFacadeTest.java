package inflean.stockissue.facade;

import inflean.stockissue.domain.Stock;
import inflean.stockissue.repository.StockRepository;
import inflean.stockissue.service.OptimisticLockStockService;
import inflean.stockissue.service.PessimisticLockStockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockStockFacadeTest {

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    // 테스트 전 상품 재고 입력
    @BeforeEach
    public void beforeTest() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    // 테스트 후 모든 재고 삭제
    @AfterEach
    public void afterTest() {
        stockRepository.deleteAll();
    }

    // 요청이 동시에 100개 들어오는 케이스 -> multi thread 사용
    @Test
    @DisplayName("재고 감소 로직 테스트 - 동시 요청 100개")
    public void decreaseStockTest2() throws InterruptedException {
        int threadCount = 100;
        // ExecutorService : 비동기로 실행하는 작업을 단순화해 사용할 수 있도로 도와주는 Java API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행 중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 요청 100개 보내기
        for(int i = 0; i < threadCount; i++){
            executorService.submit(() -> {
                try{
                    optimisticLockStockFacade.decreaseStock(1L, 1L);
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                finally{
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(0, stock.getQuantity());
    }

}