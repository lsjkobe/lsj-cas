package org.apereo.cas.config.custom.auth.configuration;

import org.apereo.cas.config.custom.auth.configurer.CustomCasWebFlowConfigurer;
import org.apereo.cas.config.custom.auth.configurer.MobileIdCasWebFlowConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@Configuration("MobileIdConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class})
public class CasWebflowExecutionConfiguration implements CasWebflowExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowRegistry;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    /**
     * Configure webflow execution plan.
     *
     * @param plan the plan
     */
    @Override
    public void configureWebflowExecutionPlan(CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(customCasWebFlowConfigurer());
        plan.registerWebflowConfigurer(mobileIdCasWebFlowConfigurer());
    }

    @Bean
    public CasWebflowConfigurer mobileIdCasWebFlowConfigurer() {
        MobileIdCasWebFlowConfigurer configurer = new MobileIdCasWebFlowConfigurer(
                flowBuilderServices, loginFlowRegistry, applicationContext, casProperties);
        configurer.initialize();
        return configurer;
    }

    @Bean
    public CasWebflowConfigurer customCasWebFlowConfigurer() {
        CustomCasWebFlowConfigurer configurer = new CustomCasWebFlowConfigurer(
                flowBuilderServices, loginFlowRegistry, applicationContext, casProperties);
        configurer.initialize();
        return configurer;
    }
}
