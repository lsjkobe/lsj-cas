package org.apereo.cas.config.custom.auth.configuration.flow.customlogin;

import lombok.val;
import org.apereo.cas.config.custom.auth.configuration.flow.AbstractDefaultLoginWebflowConfigurer;
import org.apereo.cas.config.custom.auth.constant.IFlowConstant;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.History;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class CustomLoginWebflowConfigurer extends AbstractDefaultLoginWebflowConfigurer {
    public CustomLoginWebflowConfigurer(FlowBuilderServices flowBuilderServices, FlowDefinitionRegistry flowDefinitionRegistry,
                                        ConfigurableApplicationContext applicationContext, CasConfigurationProperties casProperties) {
        super(flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    public String genViewId() {
        return IFlowConstant.FLOW_ID_CUSTOM_LOGIN + "/" + IFlowConstant.FLOW_VIEW_MAIN;
    }

    @Override
    public Flow getLoginFlow() {
        return getFlow(IFlowConstant.FLOW_ID_CUSTOM_LOGIN);
    }

    /**
     * Create login form view.
     *
     * @param flow the flow
     */
    @Override
    protected void createLoginFormView(Flow flow) {
        val propertiesToBind = CollectionUtils.wrapList("username", "password");
        val binder = createStateBinderConfiguration(propertiesToBind);
        casProperties.getView().getCustomLoginFormFields()
                .forEach((field, props) -> {
                    val fieldName = String.format("customFields[%s]", field);
                    binder.addBinding(new BinderConfiguration.Binding(fieldName, props.getConverter(), props.isRequired()));
                });
        val state = createViewState(flow, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM, genViewId(), binder);
        state.getRenderActionList().add(createEvaluateAction(CasWebflowConstants.ACTION_ID_RENDER_LOGIN_FORM));
        createStateModelBinding(state, CasWebflowConstants.VAR_ID_CREDENTIAL, CustomLoginCredential.class);
        createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, CustomLoginCredential.class);
        createTransition(state);
    }

    /**
     * Create generic login success end state.
     *
     * @param flow the flow
     */
    protected void createGenericLoginSuccessEndState(final Flow flow) {
        String successViewId = IFlowConstant.FLOW_ID_CUSTOM_LOGIN + "/" + IFlowConstant.FLOW_VIEW_SUCCESS;
        val state = createEndState(flow, CasWebflowConstants.STATE_ID_VIEW_GENERIC_LOGIN_SUCCESS, successViewId);
        state.getEntryActionList().add(createEvaluateAction(CasWebflowConstants.ACTION_ID_GENERIC_SUCCESS_VIEW));
    }

    protected void createTransition(ViewState state) {
        //transition
        val transition = createTransitionForState(state, CasWebflowConstants.TRANSITION_ID_SUBMIT, CasWebflowConstants.STATE_ID_REAL_SUBMIT);
        val attributes = transition.getAttributes();
        attributes.put("bind", Boolean.TRUE);
        attributes.put("validate", Boolean.TRUE);
        attributes.put("history", History.INVALIDATE);
    }
}
