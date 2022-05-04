package org.apereo.cas.config.custom.auth.configuration;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.config.custom.auth.configuration.flow.customlogin.CustomLoginAuthenticationHandler;
import org.apereo.cas.config.custom.auth.configuration.flow.lsjtest.LsjTestAuthenticationHandler;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration("CasHandlerConfiguration")
public class CasHandlerConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Resource
    private LsjTestAuthenticationHandler lsjTestAuthenticationHandler;

    @Resource
    private CustomLoginAuthenticationHandler customLoginAuthenticationHandler;

    /**
     * configure the plan.
     *
     * @param plan the plan
     */
    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(lsjTestAuthenticationHandler);
        plan.registerAuthenticationHandler(customLoginAuthenticationHandler);
    }

}
