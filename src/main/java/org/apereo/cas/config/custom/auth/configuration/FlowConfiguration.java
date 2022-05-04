package org.apereo.cas.config.custom.auth.configuration;

import org.apereo.cas.config.custom.auth.configuration.flow.customlogin.CustomLoginConfiguration;
import org.apereo.cas.config.custom.auth.configuration.flow.lsjtest.LsjTestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({LsjTestConfiguration.class, CustomLoginConfiguration.class})
public class FlowConfiguration {
}
