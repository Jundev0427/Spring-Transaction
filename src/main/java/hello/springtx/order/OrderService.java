package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    //JPA는 트랜잭션 커밋 시점에 Order 데이터를 DB에 반영한다.
    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("order 호출");
        orderRepository.save(order);

        log.info("결제 프로세스 진입");

        if (isSystemException(order)) {
            log.info("시스템 예외 발생");
            throw new RuntimeException("시스템 예외");
        }

        if (isNotEnoughMoneyException(order)) {
            log.info("잔고부족 비지니스 예외 발생");
            order.setPayStatus("대기");
            throw new NotEnoughMoneyException("잔고가 부족합니다");
        }

        if (isSuccess(order)) {
            log.info("정상 승인");
            order.setPayStatus("완료");
        }

        log.info("결제 프로세스 완료");
    }

    private boolean isSystemException(Order order) {
        return order.getUsername().equals("예외");
    }

    private boolean isNotEnoughMoneyException(Order order) {
        return order.getUsername().equals("잔고부족");
    }

    private boolean isSuccess(Order order) {
        return !isSystemException(order) || !isNotEnoughMoneyException(order);
    }

}
