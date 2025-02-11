
## 🛒📦재고시스템으로 알아보는 동시성 이슈 해결 방법

### Repository Method
### save()와 saveAndFlush()
- Spring Data JPA에서 엔티티를 저장할 때 사용하는 메서드
- 작동 방식의 차이가 존재

#### save()
- 트랜잭션이 끝날 때 까지 영속성 컨텍스트에 저장된 상태로 유지
- 즉시 데이터베이스에 반영❌, 영속성 컨텍스트가 Flush될 때 반영
- 트랜잭션이 끝날 때 한꺼번에 반영되므로 성능이 최적화 됨(Batch Insert⭕)

#### saveAndFlush()
- 즉시 데이터베이스에 반영됨(즉시 Flush 발생)
- 영속성 컨텍스트를 강제로 비우지 않음, 하지만 변경 사항을 DB에 반영함
- 이후 트랜잭션이 종료될 때 다시 Flush가 발생할 수 있음  
💡 `saveAndFlush()`를 불필요하게 사용하면 성능 저하(DB I/O 증가)

## 재고 감소 로직 작성
📂`Stock.java`
```java
@Entity
@Getter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // 재고 수량 감소 메서드
    public void decreaseQuantity(Long quantity) {
        if (this.quantity - quantity < 0) {
            throw new RuntimeException("재고는 0개 미만이 될 수 없습니다.");
        }

        this.quantity -= quantity;
    }
}
```

📂`StockService.java`
```java
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
```

📂`StockServiceTest.java`
```java
@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

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

    @Test
    @DisplayName("재고 감소 로직 테스트")
    public void decreaseStockTest(){

        // when
        stockService.decreaseStock(1L, 1L);

        // then
        Stock stock  = stockRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id"));

        assertAll(
                () -> assertNotNull(stock),
                () -> assertEquals(99, stock.getQuantity())
        );
    }
}
```

## 재고 감소 로직의 문제점
