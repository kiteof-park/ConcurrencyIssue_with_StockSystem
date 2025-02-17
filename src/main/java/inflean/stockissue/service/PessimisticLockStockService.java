package inflean.stockissue.service;

import inflean.stockissue.domain.Stock;
import inflean.stockissue.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    public void decreaseStock(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);
        stock.decreaseQuantity(quantity);
        stockRepository.save(stock);
    }
}
