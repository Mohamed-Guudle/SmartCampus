package com.smartcampus.exception;

/**
 * Custom exception thrown when a request references a linked resource
 * that does not exist in the system.
 *
 * For example, when creating a sensor with a roomId that does not
 * correspond to any existing room.
 *
 * This exception is mapped to HTTP 422 Unprocessable Entity to indicate
 * that the request was well-formed but contained semantically invalid
 * references.
 *
 * Student: Mohamed Guudle (W2045871)
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    // The name of the field that contained the invalid reference
    private final String fieldName;

    // The invalid value that was provided
    private final String invalidValue;

    // The type of resource that was not found
    private final String resourceType;

    /**
     * Constructs a new LinkedResourceNotFoundException.
     *
     * @param fieldName     the name of the field with the invalid reference
     * @param invalidValue  the value that was provided
     * @param resourceType  the type of resource that was not found
     */
    public LinkedResourceNotFoundException(String fieldName, String invalidValue, String resourceType) {
        super(String.format("The %s '%s' referenced in field '%s' does not exist in the system.",
                resourceType, invalidValue, fieldName));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.resourceType = resourceType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String getResourceType() {
        return resourceType;
    }
}
