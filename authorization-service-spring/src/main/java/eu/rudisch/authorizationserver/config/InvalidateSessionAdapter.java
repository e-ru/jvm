package eu.rudisch.authorizationserver.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

public class InvalidateSessionAdapter extends HandlerInterceptorAdapter {
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null
				&& modelAndView.getView() instanceof RedirectView) {
			RedirectView redirect = (RedirectView) modelAndView.getView();
			String url = redirect.getUrl();
			if (url.contains("code=") || url.contains("error=")) {
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
			}
		}
	}
}
