package conf

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.*

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}


String appPackage = context.getProperty("appPackage");
String trace = context.getProperty("ameba.trace.enabled");
logger("org.glassfish.jersey.filter.LoggingFilter", INFO)
logger("org.glassfish", WARN)
logger("org.avaje.ebean", WARN)
logger("org.avaje.ebean.SQL", TRACE)
logger("org.avaje.ebean.TXN", TRACE)
logger("httl", WARN)
logger("ameba", "true".equalsIgnoreCase(trace) ? TRACE : DEBUG)
if (appPackage != null)
    logger(appPackage, TRACE)
root(WARN, ["CONSOLE"])