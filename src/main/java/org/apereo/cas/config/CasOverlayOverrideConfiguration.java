package org.apereo.cas.config;

import org.apereo.cas.config.custom.auth.configuration.CasHandlerConfiguration;
import org.apereo.cas.config.custom.auth.configuration.CasWebflowExecutionConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration("CasOverlayOverrideConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class})
@Import({CasHandlerConfiguration.class, CasWebflowExecutionConfiguration.class})
public class CasOverlayOverrideConfiguration {

}
