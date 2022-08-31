package com.github.gkonst.logging;

import org.springframework.expression.Expression;

class LoggingAttributes {
    private final Expression before;
    private final Expression after;
    private final String logName;
    private final LogLevel logLevel;

    LoggingAttributes(Expression before, Expression after, String logName, LogLevel logLevel) {
        this.before = before;
        this.after = after;
        this.logName = logName;
        this.logLevel = logLevel;
    }

    public Expression getBefore() {
        return before;
    }

    public Expression getAfter() {
        return after;
    }

    public String getLogName() {
        return logName;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}
