package org.apereo.cas.config.custom.auth.configuration;

import org.apereo.cas.config.custom.auth.configuration.flow.LsjTestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({LsjTestConfiguration.class})
public class FlowConfiguration {
}
