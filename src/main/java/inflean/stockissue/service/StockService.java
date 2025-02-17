package inflean.stockissue.service;

import inflean.stockissue.domain.Stock;
import inflean.stockissue.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    // NamedLock - 부모의 트랜잭션과 별도로 실행되어야 하므로 propagation을 변경
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseStock(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));

        stock.decreaseQuantity(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
