package org.apereo.cas.config.custom.auth.configuration.flow.lsjtest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class LsjTestAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {


    public LsjTestAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    /**
     * Authenticates a username/password credential by an arbitrary strategy with extra parameter original credential password before
     * encoding password. Override it if implementation need to use original password for authentication.
     *
     * @param credential       the credential object bearing the transformed username and password.
     * @param originalPassword original password from credential before password encoding
     * @return AuthenticationHandlerExecutionResult resolved from credential on authentication success or null if no principal could be resolved
     * from the credential.
     * @throws GeneralSecurityException On authentication failure.
     * @throws PreventedException       On the indeterminate case when authentication is prevented.
     */
    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws GeneralSecurityException, PreventedException {
        String username = credential.getUsername();
        Principal principal = this.principalFactory.createPrincipal(username, new HashMap<>());
        String flag = "a";
        if (StringUtils.equals(flag, "a")) {
            return createHandlerResult(credential, principal, new ArrayList<>(0));
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof LsjTestCredential;
    }
}
