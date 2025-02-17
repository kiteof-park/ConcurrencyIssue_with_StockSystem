package inflean.stockissue.repository;

import inflean.stockissue.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 편의상 Stock엔티티 사용
// 실무에서는 별도의 JDBC를 사용하거나 다른 방법을 고안해야 함! - 왜 ?
// 또한 별도의 데이터 소스를 분리해서 사용해야 함 - 같은 데이터 소스 사용 시 커넥션 풀 부족 현상
public interface LockRepository extends JpaRepository<Stock, Long> {
    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
