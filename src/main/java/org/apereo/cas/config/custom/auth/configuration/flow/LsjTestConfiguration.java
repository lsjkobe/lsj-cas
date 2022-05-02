package org.apereo.cas.config.custom.auth.configuration.flow;

import lombok.val;
import org.apereo.cas.config.custom.auth.constant.IFlowConstant;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.web.flow.CasDefaultFlowUrlHandler;
import org.apereo.cas.web.flow.CasFlowHandlerAdapter;
import org.apereo.cas.web.flow.CasFlowHandlerMapping;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.executor.WebflowExecutorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.executor.FlowExecutor;

@Configuration
public class LsjTestConfiguration {

    private static final FlowExecutionListener[] FLOW_EXECUTION_LISTENERS = new FlowExecutionListener[0];

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private FlowBuilderServices flowBuilderServices;


    @Autowired
    private FlowBuilder flowBuilder;

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public FlowDefinitionRegistry lsjtestFlowRegistry() {
        val builder = new FlowDefinitionRegistryBuilder(applicationContext, flowBuilderServices);
        builder.addFlowBuilder(flowBuilder, IFlowConstant.FLOW_ID_LSJTEST);
        return builder.build();
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public HandlerMapping lsjtestHandlerMapping(CasWebflowExecutionPlan plan,
                                                FlowDefinitionRegistry lsjtestFlowRegistry) {
        val handler = new CasFlowHandlerMapping();
        handler.setOrder(1);
        handler.setFlowRegistry(lsjtestFlowRegistry);
        handler.setInterceptors(plan.getWebflowInterceptors().toArray());
        return handler;
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public HandlerAdapter lsjtestHandlerAdapter(CipherExecutor webflowCipherExecutor,
                                                FlowDefinitionRegistry lsjtestFlowRegistry) {
        val handler = new CasFlowHandlerAdapter(IFlowConstant.FLOW_ID_LSJTEST);
        handler.setFlowExecutor(lsjtestFlowExecutor(casProperties, webflowCipherExecutor, lsjtestFlowRegistry));
        handler.setFlowUrlHandler(new CasDefaultFlowUrlHandler());
        return handler;
    }


    public FlowExecutor lsjtestFlowExecutor(
            final CasConfigurationProperties casProperties,
            final CipherExecutor webflowCipherExecutor,
            FlowDefinitionRegistry lsjtestFlowRegistry) {
        val factory = new WebflowExecutorFactory(casProperties.getWebflow(),
                lsjtestFlowRegistry, webflowCipherExecutor,
                FLOW_EXECUTION_LISTENERS);
        return factory.build();
    }
}
