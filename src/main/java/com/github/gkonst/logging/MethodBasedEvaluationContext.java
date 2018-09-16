package com.github.gkonst.logging;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

public class MethodBasedEvaluationContext extends StandardEvaluationContext {
    private final Method method;
    private final Object[] args;
    private final ParameterNameDiscoverer paramDiscoverer;
    private boolean paramLoaded = false;

    public MethodBasedEvaluationContext(Object rootObject, Method method, Object[] args, ParameterNameDiscoverer paramDiscoverer) {
        super(rootObject);
        this.method = method;
        this.args = args;
        this.paramDiscoverer = paramDiscoverer;
    }

    @Override
    public Object lookupVariable(String name) {
        Object variable = super.lookupVariable(name);
        if (variable != null) {
            return variable;
        }
        if (!this.paramLoaded) {
            lazyLoadArguments();
            this.paramLoaded = true;
            variable = super.lookupVariable(name);
        }
        return variable;
    }

    /**
     * Load the param information only when needed.
     */
    protected void lazyLoadArguments() {

        setVariable("method", method);

        if (ObjectUtils.isEmpty(this.args)) {
            return;
        }

        // save arguments as indexed variables
        for (int i = 0; i < this.args.length; i++) {
            setVariable("a" + i, this.args[i]);
            setVariable("p" + i, this.args[i]);
        }

        StringBuilder builder = new StringBuilder();

        String[] parameterNames = this.paramDiscoverer.getParameterNames(this.method);

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                setVariable(parameterNames[i], this.args[i]);
                if (i > 0) {
                    builder.append(", ");
                }
                builder
                        .append(parameterNames[i])
                        .append(" : ")
                        .append(getValueWrapped(this.args[i]));
            }
        } else {
            for (int i = 0; i < this.args.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder
                        .append("a")
                        .append(i)
                        .append(" : ")
                        .append(getValueWrapped(this.args[i]));
            }
        }

        setVariable("arguments", builder.toString());
    }

    private String getValueWrapped(Object arg) {
        return '\"' + String.valueOf(arg) + '\"';
    }
}
