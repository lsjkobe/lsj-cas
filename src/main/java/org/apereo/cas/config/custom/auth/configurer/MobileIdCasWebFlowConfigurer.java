package org.apereo.cas.config.custom.auth.configurer;

import org.apereo.cas.config.custom.auth.credential.MobileIdCredential;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class MobileIdCasWebFlowConfigurer extends AbstractCasWebflowConfigurer {
    public MobileIdCasWebFlowConfigurer(FlowBuilderServices flowBuilderServices,
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
//        final Flow flow = super.getLoginFlow();
//        bindCredential(flow);
    }

    protected void bindCredential(Flow flow) {
//        // 重写绑定自定义credential
//        // 重写绑定自定义credential
//        createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, MobileIdCredential.class);
//        // 登录页绑定新参数
//        final ViewState state = (ViewState) flow.getState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
//        final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
//        // 由于用户名以及密码已经绑定，所以只需对新加系统参数绑定即可
//        // 字段名，转换器，是否必须字段
//        cfg.addBinding(new BinderConfiguration.Binding("phoneNumber", null, true));
//        cfg.addBinding(new BinderConfiguration.Binding("validateCode", null, true));
    }
}
