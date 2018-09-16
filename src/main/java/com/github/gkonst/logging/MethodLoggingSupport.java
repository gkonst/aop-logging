package com.github.gkonst.logging;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotationAttributes;


abstract class MethodLoggingSupport implements BeanFactoryAware, EnvironmentAware {

    protected final ConcurrentHashMap<Method, LoggingAttributes> cache;
    protected final ParameterNameDiscoverer parameterNameDiscoverer;
    protected final SpelExpressionParser parser;
    protected Environment environment;
    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public MethodLoggingSupport() {
        this.cache = new ConcurrentHashMap<>();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        parser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED, this.getClass().getClassLoader()));
    }

    protected Object processWithLogging(Object target, Method method, Object[] arguments, ThrowingSupplier<Object> callback) throws Throwable {
        Class<?> targetClass = AopUtils.getTargetClass(target);
        Method targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        //Caching logging attributes not to use reflection each time the method calls
        LoggingAttributes loggingAttributes = cache.computeIfAbsent(targetMethod, m -> getLoggingAttributes(targetClass, m));
        EvaluationContext context = getContext(target, targetMethod, arguments);

        return new MethodLoggingDecorator(loggingAttributes, context, callback).process();
    }

    private EvaluationContext getContext(Object target, Method method, Object[] parameters) {
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(target, method, parameters, parameterNameDiscoverer);
        context.setBeanResolver(getBeanResolver());
        return context;
    }

    private BeanResolver getBeanResolver() {
        if (beanFactory != null) {
            // to allow access to beans (@beanName)
            return new BeanFactoryResolver(beanFactory);
        }
        return null;
    }

    private LoggingAttributes getLoggingAttributes(Class targetClass, Method method) {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(method, Logging.class, false, false);

        LogLevel logLevel = (LogLevel) attributes.get("logLevel");
        @SuppressWarnings("unchecked")
        Class<Throwable>[] excludedClasses = (Class<Throwable>[]) attributes.get("noLogFor");
        String beforeTemplate = attributes.getString("logBefore");
        String afterTemplate = attributes.getAliasedString("logAfter", Logging.class, null);
        String logName = (targetClass == null ? method.getDeclaringClass() : targetClass).getName();

        Expression before = parse(beforeTemplate);
        Expression after = parse(afterTemplate);

        return new LoggingAttributes(before, after, logName, logLevel, excludedClasses);
    }

    private Expression parse(String template) {
        if (!StringUtils.isEmpty(template)) {
            if (environment != null) {
                //to allow ${propertyName} placeholders
                template = environment.resolvePlaceholders(template);
            }
            return parser.parseExpression(template, new TemplateParserContext());
        }
        return null;
    }
}
