package org.apereo.cas.config.custom.auth.configuration.flow.lsjtest;

import lombok.val;
import org.apereo.cas.config.custom.auth.constant.IFlowConstant;
import org.apereo.cas.config.custom.auth.credential.CustomCredential;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.DefaultLoginWebflowConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.History;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class LsjTestLoginWebflowConfigurer extends DefaultLoginWebflowConfigurer {
    public LsjTestLoginWebflowConfigurer(FlowBuilderServices flowBuilderServices,
                                         FlowDefinitionRegistry flowDefinitionRegistry,
                                         ConfigurableApplicationContext applicationContext,
                                         CasConfigurationProperties casProperties) {
        super(flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    public Flow getLoginFlow() {
        return getFlow(IFlowConstant.FLOW_ID_LSJTEST);
    }

    /**
     * Create login form view.
     *
     * @param flow the flow
     */
    @Override
    protected void createLoginFormView(Flow flow) {
        val propertiesToBind = CollectionUtils.wrapList("username", "password", "source");
        val binder = createStateBinderConfiguration(propertiesToBind);

        casProperties.getView().getCustomLoginFormFields()
                .forEach((field, props) -> {
                    val fieldName = String.format("customFields[%s]", field);
                    binder.addBinding(new BinderConfiguration.Binding(fieldName, props.getConverter(), props.isRequired()));
                });

        val state = createViewState(flow, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM, "lsjtest/casLoginView", binder);
        state.getRenderActionList().add(createEvaluateAction(CasWebflowConstants.ACTION_ID_RENDER_LOGIN_FORM));
        createStateModelBinding(state, CasWebflowConstants.VAR_ID_CREDENTIAL, CustomCredential.class);
        createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, CustomCredential.class);
        bindCredential(flow, state);
    }

    protected void bindCredential(Flow flow, ViewState state) {
        // 重写绑定自定义credential
//        final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
        // 由于用户名以及密码已经绑定，所以只需对新加系统参数绑定即可
        // 字段名，转换器，是否必须字段
//        cfg.addBinding(new BinderConfiguration.Binding("captcha", null, true));
        //transition
        val transition = createTransitionForState(state, CasWebflowConstants.TRANSITION_ID_SUBMIT, CasWebflowConstants.STATE_ID_REAL_SUBMIT);
        val attributes = transition.getAttributes();
        attributes.put("bind", Boolean.TRUE);
        attributes.put("validate", Boolean.TRUE);
        attributes.put("history", History.INVALIDATE);
    }
}
