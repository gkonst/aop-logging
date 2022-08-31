package com.github.gkonst.logging;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class MethodLoggingInterceptor extends MethodLoggingSupport implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Object target = invocation.getThis();
        final Method method = invocation.getMethod();
        final Object[] arguments = invocation.getArguments();
        final ThrowingSupplier<Object> callback = invocation::proceed;
        return processWithLogging(target, method, arguments, callback);
    }
}
