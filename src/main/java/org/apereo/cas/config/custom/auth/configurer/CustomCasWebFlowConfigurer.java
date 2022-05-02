package org.apereo.cas.config.custom.auth.configurer;

import lombok.val;
import org.apereo.cas.config.custom.auth.constant.IFlowConstant;
import org.apereo.cas.config.custom.auth.credential.CustomCredential;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.apereo.cas.util.CollectionUtils;

public class CustomCasWebFlowConfigurer extends AbstractCasWebflowConfigurer {
    public CustomCasWebFlowConfigurer(FlowBuilderServices flowBuilderServices,
                                      FlowDefinitionRegistry mainFlowDefinitionRegistry,
                                      ConfigurableApplicationContext applicationContext,
                                      CasConfigurationProperties casProperties) {
        super(flowBuilderServices, mainFlowDefinitionRegistry, applicationContext, casProperties);
    }

    /**
     * Handle the initialization of the webflow.
     */
    @Override
    protected void doInitialize() {
        final Flow flow = getLoginFlow();
        createLoginFormView(flow);
//        bindCredential(flow);
    }

    @Override
    public Flow getLoginFlow() {
        return getFlow(IFlowConstant.FLOW_ID_LSJTEST);
    }

    protected void createLoginFormView(Flow flow) {
        val propertiesToBind = CollectionUtils.wrapList("username", "password", "source");
        val binder = createStateBinderConfiguration(propertiesToBind);

        casProperties.getView().getCustomLoginFormFields()
                .forEach((field, props) -> {
                    val fieldName = String.format("customFields[%s]", field);
                    binder.addBinding(new BinderConfiguration.Binding(fieldName, props.getConverter(), props.isRequired()));
                });

        val state = createViewState(flow, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM, "lsjtest/casLoginView", binder);
        bindCredential(flow, state);
    }

    protected void bindCredential(Flow flow, ViewState state) {
        // 重写绑定自定义credential
        // 重写绑定自定义credential
        createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, CustomCredential.class);
        final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
        // 由于用户名以及密码已经绑定，所以只需对新加系统参数绑定即可
        // 字段名，转换器，是否必须字段
        cfg.addBinding(new BinderConfiguration.Binding("captcha", null, true));
    }
}
