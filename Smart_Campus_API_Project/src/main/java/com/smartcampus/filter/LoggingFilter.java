package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Logging filter that intercepts all incoming requests and outgoing responses.
 * Implements both ContainerRequestFilter and ContainerResponseFilter to provide
n * full observability of API traffic.
 *
 * Logs for each request:
 * - HTTP method (GET, POST, DELETE, etc.)
 * - Request URI path
 *
 * Logs for each response:
 * - HTTP status code
 *
 * This is a cross-cutting concern that applies to all endpoints automatically
 * without needing to modify any resource methods.
 *
 * Student: Mohamed Guudle (W2045871)
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // Logger instance for this class
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Intercepts incoming requests and logs the HTTP method and URI.
     * This is called before the request reaches the resource method.
     *
     * @param requestContext the context of the incoming request
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        LOGGER.info(String.format("[REQUEST] %s %s", method, uri));
    }

    /**
     * Intercepts outgoing responses and logs the HTTP status code.
     * This is called after the resource method has executed.
     *
     * @param requestContext  the context of the original request
     * @param responseContext the context of the outgoing response
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        int status = responseContext.getStatus();

        LOGGER.info(String.format("[RESPONSE] %s %s -> Status: %d", method, uri, status));
    }
}
