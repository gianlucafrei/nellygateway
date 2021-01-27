package ch.gianlucafrei.nellygateway.services.csrf;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component("csrf-none-validation")
public class CsrfNoneValidation implements CsrfProtectionValidation {

    public static final String NAME = "none";

    @Override
    public boolean shouldBlockRequest(ServerWebExchange exchange) {

        return false;
    }
}
