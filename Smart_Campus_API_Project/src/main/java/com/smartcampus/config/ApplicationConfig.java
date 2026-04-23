package com.smartcampus.config;

import com.smartcampus.filter.LoggingFilter;
import com.smartcampus.mapper.GlobalExceptionMapper;
import com.smartcampus.mapper.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.mapper.RoomNotEmptyExceptionMapper;
import com.smartcampus.mapper.SensorUnavailableExceptionMapper;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application configuration class.
 * Registers all resource classes, exception mappers, and filters.
 *
 * The @ApplicationPath annotation defines the base URI for all resources.
 * All endpoints will be prefixed with /api/v1
 *
 * Student: Mohamed Guudle (W2045871)
 */
@ApplicationPath("/api/v1")
public class ApplicationConfig extends Application {

    /**
     * Returns a set of all resource classes, providers, and filters
     * that should be registered with the JAX-RS runtime.
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Register Jackson for JSON serialization/deserialization
        classes.add(JacksonFeature.class);

        // --- Resource classes ---
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        // SensorReadingResource is instantiated dynamically via sub-resource locator,
        // so it does not need to be registered here.

        // --- Exception Mappers ---
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);

        // --- Filters ---
        classes.add(LoggingFilter.class);

        return classes;
    }
}
