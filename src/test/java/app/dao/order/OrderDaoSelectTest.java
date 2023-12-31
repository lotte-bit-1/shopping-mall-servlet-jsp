package app.dao.order;

import static org.junit.jupiter.api.Assertions.*;

import app.dto.order.response.ProductOrderDetailDto;
import app.dto.order.response.ProductOrderDto;
import app.entity.Order;
import config.TestConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.GetSessionFactory;

public class OrderDaoSelectTest {

  private final OrderDao orderDao = new OrderDao();
  private SqlSession session;
  private final TestConfig testConfig = new TestConfig();

  @BeforeEach
  void beforeEach() throws Exception {
    session = GetSessionFactory.getInstance().openSession();
    testConfig.init("schema.sql", session);
    testConfig.init("order/init-order-data.sql", session);
  }

  @AfterEach
  void afterEach() throws Exception {
    session = GetSessionFactory.getInstance().openSession();
    testConfig.init("clear-data.sql", session);
  }

  @Test
  @DisplayName("주문 조회 테스트 - 정상 처리")
  void selectById() throws Exception {
    // given
    Long orderId = 1L;

    // when,
    Optional<Order> optionalOrder = orderDao.selectById(orderId, session);
    session.commit();
    session.close();

    // then
    assertNotNull(optionalOrder.orElse(null));
    assertSame(optionalOrder.get().getId(), orderId);
  }

  @Test
  @DisplayName("주문 조회 테스트 - 존재하지 않는 사용자")
  void selectByIdEx1() throws Exception {
    // given
    Long orderId = 1000L;

    // when, then
    assertThrows(
        Exception.class, () -> orderDao.selectById(orderId, session).orElseThrow(Exception::new));
    session.commit();
    session.close();
  }

  @Test
  @DisplayName("주문 모두 조회 테스트 - 정상 처리")
  void selectAll() throws Exception {
    // given, when
    List<Order> orders = orderDao.selectAll(session);
    session.commit();
    session.close();

    // then
    assertSame(orders.size(), 5);
  }

  @Test
  @DisplayName("회원 id로 주문 모두 조회 테스트 - 정상 처리")
  void selectProductOrdersForMemberCurrentYear() throws Exception {
    // given
    Long memberId = 1L;

    // when
    List<ProductOrderDto> productOrderDtos =
        orderDao.selectProductOrdersForMemberCurrentYear(memberId, session);
    session.commit();
    session.close();

    // then
    assertSame(productOrderDtos.size(), 1);
    assertSame(productOrderDtos.get(0).getProducts().size(), 5);
  }

  @Test
  @DisplayName("주문 id, 회원 id로 주문 조회 테스트 - 정상 처리")
  void selectOrderDetailsForMemberAndOrderId() throws Exception {
    // given
    final Map<String, Long> orderIdAndMemberIdParameterMap = new HashMap<>();
    orderIdAndMemberIdParameterMap.put("orderId", 1L);
    orderIdAndMemberIdParameterMap.put("memberId", 1L);

    // when

    Optional<ProductOrderDetailDto> optionalProductOrderDetailDto =
        orderDao.selectOrderDetailsForMemberAndOrderId(orderIdAndMemberIdParameterMap, session);
    session.commit();
    session.close();

    // then
    assertTrue(optionalProductOrderDetailDto.isPresent());
    assertSame(optionalProductOrderDetailDto.get().getOrderId(), 1L);
  }
}
