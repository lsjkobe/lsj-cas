package org.apereo.cas.config.custom.auth.configuration.flow.customlogin;

import lombok.val;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.config.custom.auth.constant.IFlowConstant;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.web.flow.*;
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

import javax.annotation.Resource;

@Configuration
public class CustomLoginConfiguration {

    private static final FlowExecutionListener[] FLOW_EXECUTION_LISTENERS = new FlowExecutionListener[0];

    @Resource
    private CasConfigurationProperties casProperties;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Resource
    private FlowBuilderServices flowBuilderServices;


    @Resource
    private FlowBuilder flowBuilder;

    @Resource
    private CasWebflowExecutionPlan plan;

    @Resource
    private CipherExecutor webflowCipherExecutor;

    @Resource
    private ServicesManager servicesManager;

    @Bean
    public CasWebflowConfigurer customLoginWebflowConfigurer() {
        CustomLoginWebflowConfigurer configurer = new CustomLoginWebflowConfigurer(
                flowBuilderServices, customLoginFlowRegistry(), applicationContext, casProperties);
        configurer.initialize();
        return configurer;
    }

    /**
     * 处理器.
     *
     * @return .
     */
    @Bean
    public CustomLoginAuthenticationHandler customLoginAuthenticationHandler() {
        return new CustomLoginAuthenticationHandler(
                CustomLoginAuthenticationHandler.class.getName(), servicesManager, new DefaultPrincipalFactory(), 1);
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public FlowDefinitionRegistry customLoginFlowRegistry() {
        val builder = new FlowDefinitionRegistryBuilder(applicationContext, flowBuilderServices);
        builder.addFlowBuilder(flowBuilder, IFlowConstant.FLOW_ID_CUSTOM_LOGIN);
        return builder.build();
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public HandlerMapping customLoginHandlerMapping() {
        val handler = new CasFlowHandlerMapping();
        handler.setOrder(1);
        handler.setFlowRegistry(customLoginFlowRegistry());
        handler.setInterceptors(plan.getWebflowInterceptors().toArray());
        return handler;
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public HandlerAdapter customLoginHandlerAdapter() {
        val handler = new CasFlowHandlerAdapter(IFlowConstant.FLOW_ID_CUSTOM_LOGIN);
        handler.setFlowExecutor(customLoginFlowExecutor());
        handler.setFlowUrlHandler(new CasDefaultFlowUrlHandler());
        return handler;
    }


    public FlowExecutor customLoginFlowExecutor() {
        val factory = new WebflowExecutorFactory(casProperties.getWebflow(),
                customLoginFlowRegistry(), webflowCipherExecutor, FLOW_EXECUTION_LISTENERS);
        return factory.build();
    }
}
