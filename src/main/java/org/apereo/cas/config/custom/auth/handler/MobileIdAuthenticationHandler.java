package org.apereo.cas.config.custom.auth.handler;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.config.custom.auth.credential.MobileIdCredential;
import org.apereo.cas.services.ServicesManager;

public class MobileIdAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    public MobileIdAuthenticationHandler(String name, ServicesManager servicesManager,
                                            PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    /**
     * Performs the details of authentication and returns an authentication handler result on success.
     *
     * @param credential Credential to authenticate.
     * @return Authentication handler result on success.
     * @throws PreventedException       On the indeterminate case when authentication is prevented.
     */
    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws PreventedException {
        MobileIdCredential mobileIdCredential = (MobileIdCredential) credential;
        return null;
    }

    /**
     * Determines whether the handler has the capability to authenticate the given credential. In practical terms,
     * the {@link #authenticate(Credential)} method MUST be capable of processing a given credential if
     * {@code supports} returns true on the same credential.
     *
     * @param credential The credential to check.
     * @return True if the handler supports the Credential, false otherwise.
     */
    @Override
    public boolean supports(Credential credential) {
        return credential instanceof MobileIdCredential;
    }
}
