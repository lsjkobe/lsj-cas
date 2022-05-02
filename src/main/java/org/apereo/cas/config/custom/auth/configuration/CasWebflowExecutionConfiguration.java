package org.apereo.cas.config.custom.auth.configuration;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration("CustomCasWebflowExecutionConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class})
public class CasWebflowExecutionConfiguration implements CasWebflowExecutionPlanConfigurer {

    @Resource
    private CasWebflowConfigurer customCasWebFlowConfigurer;

    /**
     * Configure webflow execution plan.
     *
     * @param plan the plan
     */
    @Override
    public void configureWebflowExecutionPlan(CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(customCasWebFlowConfigurer);
    }

}
