package web.controller;

import app.dto.order.form.OrderCartCreateForm;
import app.dto.order.form.OrderCreateForm;
import app.dto.order.response.ProductOrderDetailDto;
import app.dto.order.response.ProductOrderDto;
import app.dto.member.response.MemberDetail;
import app.exception.DomainException;
import app.exception.order.OrderProductNotEnoughStockQuantityException;
import app.service.order.OrderService;
import web.ControllerFrame;
import web.dispatcher.Navi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class OrderController implements ControllerFrame {

  private final OrderService orderService = new OrderService();
  private Long memberId;

  public OrderController() {
    super();
  }

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String view = request.getParameter("view");
    String cmd = request.getParameter("cmd");

    HttpSession session = request.getSession();
    MemberDetail loginMember = (MemberDetail) session.getAttribute("loginMember");

    String viewName = Navi.REDIRECT_MAIN;

    if (loginMember == null) {
      viewName = Navi.REDIRECT_MAIN;
    }

    if (loginMember != null && view != null && cmd != null) {
      memberId = loginMember.getId();
      viewName = build(request, response, view, cmd);
    }

    return viewName;
  }

  private String build(
      HttpServletRequest request, HttpServletResponse response, String view, String cmd)
      throws UnsupportedEncodingException {
    if (view.equals("direct") && cmd.equals("form")) {
      return getCreateOrderForm(request, response);
    } else if (view.equals("cart") && cmd.equals("form")) {
      return getCreateCartOrderForm(request, response);
    } else if (view.equals("detail") && cmd.equals("get")) {
      return getProductOrderDetail(request, response);
    } else if (view.equals("list") && cmd.equals("get")) {
      return getProductOrders(request, response);
    }

    return Navi.REDIRECT_MAIN;
  }

  // TODO: 상품 주문 폼
  private String getCreateOrderForm(HttpServletRequest request, HttpServletResponse response)
      throws UnsupportedEncodingException {
    Long productId = 0L;
    try {
      productId = Long.parseLong(request.getParameter("productId"));
      Long quantity = Long.parseLong(request.getParameter("quantity"));

      OrderCreateForm createOrderForm = orderService.getCreateOrderForm(memberId, productId);
      if (createOrderForm.getProduct().getQuantity() - quantity < 0) {
        throw new OrderProductNotEnoughStockQuantityException();
      }
      request.setAttribute("memberName", createOrderForm.getMemberName());
      request.setAttribute("defaultAddress", createOrderForm.getDefaultAddress());
      request.setAttribute("product", createOrderForm.getProduct());
      request.setAttribute("productQuantity", quantity);
      request.setAttribute("coupons", createOrderForm.getCoupons());

      return Navi.FORWARD_ORDER_FORM;
    } catch (DomainException e) {
      if(productId == 0L) return Navi.REDIRECT_MAIN;
      else return String.format(Navi.REDIRECT_SHOP_DETAIL, productId)
          + String.format("&errorMessage=%s", URLEncoder.encode(e.getMessage(), "UTF-8"));
    } catch (Exception e) {
      return Navi.REDIRECT_MAIN
          + String.format("?errorMessage=%s", URLEncoder.encode("시스템 에러", "UTF-8"));
    }
  }

  // TODO: 장바구니 상품 주문 폼
  private String getCreateCartOrderForm(HttpServletRequest request, HttpServletResponse response)
      throws UnsupportedEncodingException {
    try {
      OrderCartCreateForm createCartOrderForm = orderService.getCreateCartOrderForm(memberId);
      request.setAttribute("memberName", createCartOrderForm.getMemberName());
      request.setAttribute("defaultAddress", createCartOrderForm.getDefaultAddress());
      request.setAttribute("products", createCartOrderForm.getProducts());
      request.setAttribute("coupons", createCartOrderForm.getCoupons());

      return Navi.FORWARD_ORDER_CART_FORM;
    } catch (DomainException e) {
      return Navi.REDIRECT_CART_FORM
          + String.format("?errorMessage=%s", URLEncoder.encode(e.getMessage(), "UTF-8"));
    } catch (Exception e) {
      return Navi.REDIRECT_MAIN
          + String.format("?errorMessage=%s", URLEncoder.encode("시스템 에러", "UTF-8"));
    }
  }

  private String getProductOrders(HttpServletRequest request, HttpServletResponse response)
      throws UnsupportedEncodingException {
    try {
      List<ProductOrderDto> productOrders =
          orderService.getProductOrdersForMemberCurrentYear(memberId);
      request.setAttribute("productOrders", productOrders);
      return Navi.FORWARD_ORDER_LIST;
    } catch (DomainException e) {
      return Navi.REDIRECT_MAIN
          + String.format("?errorMessage=%s", URLEncoder.encode(e.getMessage(), "UTF-8"));
    } catch (Exception e) {
      return Navi.REDIRECT_MAIN
          + String.format("?errorMessage=%s", URLEncoder.encode("시스템 에러", "UTF-8"));
    }
  }

  private String getProductOrderDetail(HttpServletRequest request, HttpServletResponse response)
      throws UnsupportedEncodingException {
    try {
      Long orderId = Long.parseLong(request.getParameter("orderId"));
      ProductOrderDetailDto productOrderDetail =
          orderService.getOrderDetailsForMemberAndOrderId(orderId, memberId);
      request.setAttribute("products", productOrderDetail.getProducts());
      request.setAttribute("payment", productOrderDetail.getPayment());
      request.setAttribute("delivery", productOrderDetail.getDelivery());
      request.setAttribute("productOrderDetail", productOrderDetail);
      return Navi.FORWARD_ORDER_DETAIL;
    } catch (DomainException e) {
      return Navi.REDIRECT_ORDER_LIST
          + String.format("?errorMessage=%s", URLEncoder.encode(e.getMessage(), "UTF-8"));
    } catch (Exception e) {
      return Navi.REDIRECT_MAIN
          + String.format("?errorMessage=%s", URLEncoder.encode("시스템 에러", "UTF-8"));
    }
  }
}
