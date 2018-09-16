package com.github.gkonst.logging;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
public class AspectJMethodLoggingConfiguration extends AbstractMethodLoggingConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public MethodLoggingAspect loggingAspect() {
        return new MethodLoggingAspect();
    }
}
