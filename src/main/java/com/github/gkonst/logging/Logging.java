package com.github.gkonst.logging;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

    String BEFORE = "#{#method.getName()} (#{#arguments?:''}) is called...";
    String AFTER = "#{#method.getName()} is finished in #{#duration.toMillis()} ms";
    @AliasFor("logAfter")
    String value() default AFTER;

    String logBefore() default BEFORE;

    @AliasFor("value")
    String logAfter() default AFTER;

    LogLevel logLevel() default LogLevel.DEBUG;

    Class<? extends Throwable>[] noLogFor() default {};
}
