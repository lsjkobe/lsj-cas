package org.apereo.cas.config.custom.auth.configuration.flow.customlogin;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalNameTransformerUtils;
import org.apereo.cas.authentication.support.password.PasswordEncoderUtils;
import org.apereo.cas.authentication.support.password.PasswordPolicyContext;
import org.apereo.cas.config.custom.auth.constant.IFlowConstant;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.BaseJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryJdbcAuthenticationProperties;
import org.apereo.cas.configuration.support.JpaBeans;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Configuration("CustomLoginConfiguration")
public class CustomLoginConfiguration implements AuthenticationEventExecutionPlanConfigurer {

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

    @Resource
    private PasswordPolicyContext queryPasswordPolicyConfiguration;

    /**
     * 注册验证处理器.
     * @param plan the plan .
     */
    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
        List<AuthenticationHandler> customHandlerList = new ArrayList<>();
        List<QueryJdbcAuthenticationProperties> query = casProperties.getAuthn().getJdbc().getQuery();
        for (QueryJdbcAuthenticationProperties jdbcProperties : query) {
            CustomQueryDBAuthenticationHandler handler = customQueryDBAuthenticationHandler(jdbcProperties);
            customHandlerList.add(handler);
        }
        for (AuthenticationHandler handler : customHandlerList) {
            plan.registerAuthenticationHandler(handler);
        }
    }

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
    public CustomQueryDBAuthenticationHandler customQueryDBAuthenticationHandler(QueryJdbcAuthenticationProperties jdbcProperties) {
        String name = jdbcProperties.getProperties().get(CustomQueryDBAuthenticationHandler.JPA_PROPERTIES_CUSTOM_NAME);
        String customOrder = jdbcProperties.getProperties().get(CustomQueryDBAuthenticationHandler.JPA_PROPERTIES_CUSTOM_ORDER);
        int orderInt = jdbcProperties.getOrder();
        if (StringUtils.isBlank(name)) {
            name = CustomQueryDBAuthenticationHandler.class.getName();
        }
        if (StringUtils.isNotBlank(customOrder)) {
            orderInt = Integer.parseInt(customOrder);
        }
        CustomQueryDBAuthenticationHandler handler =
                new CustomQueryDBAuthenticationHandler(name, servicesManager, new DefaultPrincipalFactory(), orderInt,
                jdbcProperties, JpaBeans.newDataSource(jdbcProperties));
        configureJdbcAuthenticationHandler(handler, jdbcProperties, applicationContext);
        return handler;
    }

    private void configureJdbcAuthenticationHandler(final AbstractJdbcUsernamePasswordAuthenticationHandler handler,
                                                           final BaseJdbcAuthenticationProperties properties,
                                                           final ConfigurableApplicationContext applicationContext) {
        handler.setPasswordEncoder(PasswordEncoderUtils.newPasswordEncoder(properties.getPasswordEncoder(), applicationContext));
        handler.setPrincipalNameTransformer(PrincipalNameTransformerUtils.newPrincipalNameTransformer(properties.getPrincipalTransformation()));
        handler.setPasswordPolicyConfiguration(queryPasswordPolicyConfiguration);
        handler.setState(properties.getState());
        if (StringUtils.isNotBlank(properties.getCredentialCriteria())) {
            handler.setCredentialSelectionPredicate(CoreAuthenticationUtils.newCredentialSelectionPredicate(properties.getCredentialCriteria()));
        }
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
