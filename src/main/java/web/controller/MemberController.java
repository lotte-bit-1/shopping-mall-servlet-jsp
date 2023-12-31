package web.controller;

import app.service.member.MemberService;
import web.ControllerFrame;
import web.dispatcher.Navi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MemberController implements ControllerFrame {

  private final MemberService memberService;

  public MemberController() {
    super();
    memberService = new MemberService();
  }

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String next = Navi.REDIRECT_MAIN;
    String view = request.getParameter("view");
    if (view != null) {
      next = build(request, view);
    }
    return next;
  }

  private String build(HttpServletRequest request, String view) throws Exception {
    String path = Navi.FORWARD_MAIN;
    switch (view) {
      case "registerForm":
        return registerForm();
      case "loginForm":
        return loginForm();
      case "logout":
        return logout(request);
    }
    return path;
  }

  private String registerForm() {
    return Navi.FORWARD_REGISTER_FORM;
  }

  private String loginForm() {
    return Navi.FORWARD_LOGIN_FORM;
  }

  private String logout(HttpServletRequest request) {
    HttpSession session = request.getSession();
    session.invalidate();
    return Navi.REDIRECT_MAIN;
  }
}
