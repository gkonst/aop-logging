package com.github.gkonst.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;

class MethodLoggingDecorator {
    private final LoggingAttributes attributes;
    private final EvaluationContext context;
    private final ThrowingSupplier<Object> callback;
    private final Logger logger;


    public MethodLoggingDecorator(LoggingAttributes attributes, EvaluationContext context, ThrowingSupplier<Object> callback) {
        this.attributes = attributes;
        this.context = context;
        this.callback = callback;
        logger = LoggerFactory.getLogger(attributes.getLogName());
    }

    public Object process() throws Throwable {
        log(attributes.getBefore());
        Temporal start = Instant.now();
        Object result = null;
        Throwable err = null;
        try {
            result = callback.get();
        } catch (Throwable throwable) {
            err = throwable;
            if (!isExcluded(throwable)) {
                logger.error(throwable.getMessage(), throwable);
            }
        }
        context.setVariable("duration", Duration.between(start, Instant.now()));
        context.setVariable("result", result);
        context.setVariable("exception", err);

        log(attributes.getAfter());
        if (err != null) {
            throw err;
        }
        return result;
    }

    private boolean isExcluded(Throwable throwable) {
        for (Class<? extends Throwable> excludedClass : attributes.getNoLogFor()) {
            if (excludedClass.isInstance(throwable)) {
                return true;
            }
        }
        return false;
    }

    private void log(Expression expression) {
        if (expression != null) {
            String message = expression.getValue(context, String.class);
            if (!StringUtils.isEmpty(message)) {
                switch (attributes.getLogLevel()) {
                    case ERROR:
                        logger.error(message);
                        break;
                    case WARN:
                        logger.warn(message);
                        break;
                    case INFO:
                        logger.info(message);
                        break;
                    case DEBUG:
                        logger.debug(message);
                        break;
                    case TRACE:
                        logger.trace(message);
                        break;
                }
            }
        }
    }

}
