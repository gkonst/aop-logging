package com.github.gkonst.logging;

import org.springframework.expression.Expression;

class LoggingAttributes {
    private final Expression before;
    private final Expression after;
    private final String logName;
    private final LogLevel logLevel;
    private final Class<? extends Throwable>[] noLogFor;

    LoggingAttributes(Expression before, Expression after, String logName, LogLevel logLevel, Class<? extends Throwable>[] noLogFor) {
        this.before = before;
        this.after = after;
        this.logName = logName;
        this.logLevel = logLevel;
        this.noLogFor = noLogFor;
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

    public Class<? extends Throwable>[] getNoLogFor() {
        return noLogFor;
    }
}
