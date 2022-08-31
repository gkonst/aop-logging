package com.github.gkonst.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class MethodLoggingAspect extends MethodLoggingSupport {
    @Around(value = "@annotation(LogMethod) && target(target)")
    public Object aroundLogging(ProceedingJoinPoint joinPoint, Object target) throws Throwable {
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final Object[] arguments = joinPoint.getArgs();
        final ThrowingSupplier<Object> callback = joinPoint::proceed;

        return processWithLogging(target, method, arguments, callback);
    }
}
