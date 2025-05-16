package com.thenexusreborn.api.sql.objects;

/**
 * The functional interface for a deserializer in the {@link TypeHandler} class
 */
@FunctionalInterface
public interface TypeDeserializer {
    /**
     * Deserializes a MySQL default object into a Java Object<br>
     * Please see <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html">MySQL JDBC Reference</a> for more information on what the object paramter can be
     * @param column The column that is represented by the value
     * @param object The object to be deserialized
     * @return The deserialized object
     */
    Object deserialize(Column column, Object object);
}
