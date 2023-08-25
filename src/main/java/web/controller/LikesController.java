package web.controller;

import app.dto.product.ProductListItemOfLike;
import app.dto.response.MemberDetail;
import app.exception.DomainException;
import app.service.likes.ProductLikesService;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import web.ControllerFrame;
import web.dispatcher.Navi;

public class LikesController implements ControllerFrame {

  private final ProductLikesService likesService = new ProductLikesService();
  private Long memberId;

  public LikesController() {
    super();
  }

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String next = Navi.REDIRECT_MAIN;
    String view = request.getParameter("view");

    HttpSession session = request.getSession();
    MemberDetail loginMember = (MemberDetail) session.getAttribute("loginMember");

    if (loginMember == null)
      return next;

    memberId = loginMember.getId();

    if (view.equals("likes")) {
      next = getLikes(request);
    }

    return next;
  }

  public String getLikes(HttpServletRequest request) {
    String errorMessage = "";
    try {
      errorMessage = URLEncoder.encode("시스템 에러", "UTF-8");

      List<ProductListItemOfLike> list = likesService.getMemberLikes(memberId);
      request.setAttribute("list", list);
      return Navi.FORWARD_LIKES_LIST;
    } catch (DomainException e) {
      return Navi.REDIRECT_LIKES_LIST + String.format("?errorMessage=%s", e.getMessage());
    } catch (Exception e) {
      return Navi.REDIRECT_MAIN + String.format("?errorMessage=%s", errorMessage);
    }
  }

}
