package org.owasp.oag.infrastructure.factories;

import org.owasp.oag.services.csrf.CsrfProtectionValidation;
import org.springframework.context.ApplicationContext;

public interface CsrfValidationImplementationFactory {

    CsrfProtectionValidation loadCsrfValidationImplementation(String csrfProtectionName);

    static CsrfValidationImplementationFactory get(ApplicationContext context){

        return context.getBean(CsrfValidationImplementationFactory.class);
    }
}
