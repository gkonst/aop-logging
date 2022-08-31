package com.github.gkonst.logging;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
public class AutoProxyMethodLoggingConfiguration extends AbstractMethodLoggingConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DefaultBeanFactoryPointcutAdvisor advisor() {
        DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
        advisor.setPointcut(AnnotationMatchingPointcut.forMethodAnnotation(LogMethod.class));
        advisor.setAdvice(advice());
        advisor.setOrder(this.enableLoggingFeature.<Integer>getNumber("order"));
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advice advice() {
        return new MethodLoggingInterceptor();
    }
}
