package org.apereo.cas.config.custom.auth.configuration.authentication;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.DefaultAuthenticationServiceSelectionStrategy;
import org.apereo.cas.authentication.principal.AbstractWebApplicationService;
import org.apereo.cas.authentication.principal.Service;

public class CustomDefaultAuthenticationServiceSelectionStrategy extends DefaultAuthenticationServiceSelectionStrategy {
    @Override
    public Service resolveServiceFrom(Service service) {
        if (service == null || StringUtils.isBlank(service.getId())) {
            return service;
        }
        String serviceId = service.getId();
        if (StringUtils.isNotBlank(serviceId) && serviceId.contains("?")) {
            String newId = serviceId.split("\\?")[0];
            if (service instanceof AbstractWebApplicationService) {
                ((AbstractWebApplicationService) service).setId(newId);
            }
        }
        return service;
    }
}
