package org.apereo.cas.config.custom.auth.configuration;

import org.apereo.cas.config.custom.auth.configurer.CustomCasWebFlowConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

import javax.annotation.Resource;

@Configuration("CustomCasWebflowExecutionConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class})
public class CasWebflowExecutionConfiguration implements CasWebflowExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Resource
    private FlowBuilderServices flowBuilderServices;

    @Resource
    private FlowDefinitionRegistry lsjtestFlowRegistry;

    /**
     * Configure webflow execution plan.
     *
     * @param plan the plan
     */
    @Override
    public void configureWebflowExecutionPlan(CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(customCasWebFlowConfigurer());
    }

    @Bean
    public CasWebflowConfigurer customCasWebFlowConfigurer() {
        CustomCasWebFlowConfigurer configurer = new CustomCasWebFlowConfigurer(
                flowBuilderServices, lsjtestFlowRegistry, applicationContext, casProperties);
        configurer.initialize();
        return configurer;
    }

}
