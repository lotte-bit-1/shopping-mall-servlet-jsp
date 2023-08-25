package web.controller;

import app.dto.response.MemberDetail;
import app.entity.ProductAndMemberCompositeKey;
import app.exception.likes.LikesEntityDuplicateException;
import app.service.likes.ProductLikesService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import web.RestControllerFrame;

public class LikesRestController implements RestControllerFrame {

  private final ProductLikesService likesService = new ProductLikesService();
  private Long memberId;
  private Long productId;

  public LikesRestController() {
    super();
  }

  @Override
  public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Object result = "";
    String cmd = request.getParameter("cmd");

    HttpSession session = request.getSession();
    MemberDetail loginMember = (MemberDetail) session.getAttribute("loginMember");

    if (loginMember == null) return result;

    memberId = loginMember.getId();

    if (cmd != null) {
      result = build(request, cmd);
    }

    return result;
  }

  private Object build(HttpServletRequest request, String cmd) throws Exception {
    switch (cmd) {
      case "add":
        productId = Long.parseLong(request.getParameter("productId"));
        return addLikes(request);
      case "cancel":
        productId = Long.parseLong(request.getParameter("productId"));
        return cancelLikes(request);
      case "cancelSome":
        List<Long> productIdList =
            Arrays.stream(request.getParameterValues("productIdList"))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return cancelSomeLikes(request, productIdList);
      default:
        return -1;
    }
  }

  // 찜 추가
  private Object addLikes(HttpServletRequest request) throws Exception {
    if (likesService.getMemberProductLikes(
        ProductAndMemberCompositeKey.builder().memberId(memberId).productId(productId).build()))
      throw new LikesEntityDuplicateException();

    return likesService.addLikes(
        ProductAndMemberCompositeKey.builder().memberId(memberId).productId(productId).build());
  }

  // 찜 취소
  private Object cancelLikes(HttpServletRequest request) {
    return likesService.removeLikes(
        ProductAndMemberCompositeKey.builder().memberId(memberId).productId(productId).build());
  }

  // 찜 벌크 취소
  private Object cancelSomeLikes(HttpServletRequest request, List<Long> productIdList) {
    List<ProductAndMemberCompositeKey> compKey = new ArrayList<>();
    for (Long pId : productIdList) {
      compKey.add(ProductAndMemberCompositeKey.builder().memberId(memberId).productId(pId).build());
    }
    return likesService.removeSomeLikes(compKey);
  }
}