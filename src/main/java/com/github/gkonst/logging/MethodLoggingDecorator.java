package com.github.gkonst.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;

import static org.springframework.util.StringUtils.hasText;

class MethodLoggingDecorator {
    private final LoggingAttributes attributes;
    private final EvaluationContext context;
    private final ThrowingSupplier<Object> callback;
    private final Logger logger;


    public MethodLoggingDecorator(LoggingAttributes attributes, EvaluationContext context, ThrowingSupplier<Object> callback) {
        this.attributes = attributes;
        this.context = context;
        this.callback = callback;
        this.logger = LoggerFactory.getLogger(attributes.getLogName());
    }

    public Object process() throws Throwable {
        log(attributes.getBefore());

        final Temporal start = Instant.now();
        final Object result = callback.get();

        context.setVariable("duration", Duration.between(start, Instant.now()));
        context.setVariable("result", result);

        log(attributes.getAfter());

        return result;
    }

    private void log(Expression expression) {
        if (expression != null) {
            final String message = expression.getValue(context, String.class);
            if (hasText(message)) {
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
