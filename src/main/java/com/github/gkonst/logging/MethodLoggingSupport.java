package com.github.gkonst.logging;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;
import static org.springframework.aop.support.AopUtils.getMostSpecificMethod;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.util.StringUtils.hasLength;


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
        this.parser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED, this.getClass().getClassLoader()));
    }

    protected Object processWithLogging(Object target, Method method, Object[] arguments, ThrowingSupplier<Object> callback) throws Throwable {
        final Class<?> targetClass = getTargetClass(target);
        final Method targetMethod = getMostSpecificMethod(method, targetClass);

        //Caching logging attributes not to use reflection each time the method calls
        final LoggingAttributes loggingAttributes = cache.computeIfAbsent(targetMethod, m -> getLoggingAttributes(targetClass, m));
        final EvaluationContext context = getContext(target, targetMethod, arguments);

        return new MethodLoggingDecorator(loggingAttributes, context, callback).process();
    }

    private EvaluationContext getContext(Object target, Method method, Object[] parameters) {
        final MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(target, method, parameters, parameterNameDiscoverer);
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        return context;
    }

    private LoggingAttributes getLoggingAttributes(Class<?> targetClass, Method method) {
        final LogMethod annotation = requireNonNull(findMergedAnnotation(method, LogMethod.class));

        final LogLevel logLevel = annotation.logLevel();
        final String beforeTemplate = annotation.logBefore();
        final String afterTemplate = annotation.logAfter();
        final String logName = (targetClass == null ? method.getDeclaringClass() : targetClass).getName();

        final Expression before = parse(beforeTemplate);
        final Expression after = parse(afterTemplate);

        return new LoggingAttributes(before, after, logName, logLevel);
    }

    private Expression parse(String template) {
        if (hasLength(template)) {
            if (environment != null) {
                //to allow ${propertyName} placeholders
                template = environment.resolvePlaceholders(template);
            }
            return parser.parseExpression(template, new TemplateParserContext());
        }
        return null;
    }
}
