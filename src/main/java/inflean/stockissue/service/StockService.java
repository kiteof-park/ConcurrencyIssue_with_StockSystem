package inflean.stockissue.service;

import inflean.stockissue.domain.Stock;
import inflean.stockissue.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public void decreaseStock(Long id, Long quantity) {
        // Stock을 조회
        // 재고 감소
        // 갱신된 값을 저장
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));

        stock.decreaseQuantity(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
