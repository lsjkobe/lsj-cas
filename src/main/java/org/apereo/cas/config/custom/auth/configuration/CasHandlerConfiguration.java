package org.apereo.cas.config.custom.auth.configuration;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.config.custom.auth.handler.MobileIdAuthenticationHandler;
import org.apereo.cas.config.custom.auth.handler.MyAuthenticationHandler;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("CasHandlerConfiguration")
public class CasHandlerConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    @Qualifier(value = "servicesManager")
    private ServicesManager servicesManager;

    /**
     * configure the plan.
     *
     * @param plan the plan
     */
    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) throws Exception {
        plan.registerAuthenticationHandler(myAuthenticationHandler());
        plan.registerAuthenticationHandler(mobileIdAuthenticationHandler());
    }

    @Bean
    public MyAuthenticationHandler myAuthenticationHandler() {
        return new MyAuthenticationHandler(
                MyAuthenticationHandler.class.getName(), servicesManager, new DefaultPrincipalFactory(), 1);
    }

    @Bean
    public MobileIdAuthenticationHandler mobileIdAuthenticationHandler() {
        return new MobileIdAuthenticationHandler(
                MobileIdAuthenticationHandler.class.getName(), servicesManager, new DefaultPrincipalFactory(), 1);
    }
}
