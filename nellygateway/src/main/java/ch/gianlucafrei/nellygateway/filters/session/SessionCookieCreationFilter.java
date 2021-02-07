package ch.gianlucafrei.nellygateway.filters.session;

import ch.gianlucafrei.nellygateway.GlobalClockSource;
import ch.gianlucafrei.nellygateway.config.configuration.NellyConfig;
import ch.gianlucafrei.nellygateway.cookies.CookieConverter;
import ch.gianlucafrei.nellygateway.cookies.LoginCookie;
import ch.gianlucafrei.nellygateway.services.login.drivers.UserModel;
import ch.gianlucafrei.nellygateway.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.UUID;

@Component
public class SessionCookieCreationFilter implements NellySessionFilter {

    @Autowired
    private CookieConverter cookieConverter;

    @Autowired
    private NellyConfig config;

    @Autowired
    private GlobalClockSource globalClockSource;

    @Override
    public void renewSession(Map<String, Object> filterContext, ServerHttpResponse response) {

        Session session = (Session) filterContext.get("old-session");

        filterContext.put("providerKey", session.getProvider());
        filterContext.put("userModel", session.getUserModel());

        createSession(filterContext, response); // Create a new session cookie which overwrites the old one
    }

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void createSession(Map<String, Object> filterContext, ServerHttpResponse response) {

        String providerKey = (String) filterContext.get("providerKey");
        UserModel model = (UserModel) filterContext.get("userModel");

        int currentTimeSeconds = (int) (globalClockSource.getGlobalClock().millis() / 1000);
        int sessionDuration = config.getSessionBehaviour().getSessionDuration();
        int sessionExp = currentTimeSeconds + sessionDuration;

        var sessionId = UUID.randomUUID().toString();
        LoginCookie loginCookie = new LoginCookie(sessionExp, providerKey, model, sessionId);

        // Bind csrf token to encrypted login cookie
        if (filterContext.containsKey("csrfToken")) {
            var csrfToken = (String) filterContext.get("csrfToken");
            loginCookie.setCsrfToken(csrfToken);
        }

        response.addCookie(cookieConverter.convertLoginCookie(loginCookie, sessionDuration));
    }

    @Override
    public void destroySession(Map<String, Object> filterContext, ServerWebExchange exchange) {

        // Override session cookie with new cookie that has max-age = 0
        exchange.getResponse().addCookie(cookieConverter.convertLoginCookie(null, 0));
    }
}
