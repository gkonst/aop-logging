package com.github.gkonst.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class MethodLoggingAspect extends MethodLoggingSupport {
    @Around(value = "@annotation(Logging) && target(target)")
    public Object aroundLogging(ProceedingJoinPoint joinPoint, Object target) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] arguments = joinPoint.getArgs();
        ThrowingSupplier<Object> callback = joinPoint::proceed;

        return processWithLogging(target, method, arguments, callback);
    }
}
