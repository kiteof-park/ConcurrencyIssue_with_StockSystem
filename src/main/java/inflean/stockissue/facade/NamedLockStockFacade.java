package inflean.stockissue.facade;

import inflean.stockissue.domain.Stock;
import inflean.stockissue.repository.LockRepository;
import inflean.stockissue.repository.StockRepository;
import inflean.stockissue.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;

    @Transactional
    public void decreaseStock(Long id, Long quantity) {
        try{
            // 락 획득
            lockRepository.getLock(id.toString());

            // 재고 감소
            stockService.decreaseStock(id, quantity);
        } finally {
            // 락 해제
            lockRepository.releaseLock(id.toString());
        }
    }
}
