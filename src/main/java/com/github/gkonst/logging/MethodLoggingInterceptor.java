package com.github.gkonst.logging;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class MethodLoggingInterceptor extends MethodLoggingSupport implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        ThrowingSupplier<Object> callback = invocation::proceed;
        return processWithLogging(target, method, arguments, callback);
    }
}
