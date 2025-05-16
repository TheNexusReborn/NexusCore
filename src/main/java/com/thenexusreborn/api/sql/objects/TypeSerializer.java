package com.thenexusreborn.api.sql.objects;

/**
 * The functional interface for the serializer in the {@link TypeHandler} class
 */
@FunctionalInterface
public interface TypeSerializer {
    /**
     * Serializes an object into a MySQL Supported Type <br>
     * @param column The column that the object is represented by
     * @param object The actual object
     * @return A supported JDBC conversion type. Please see <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html">MySQL JDBC Reference</a>
     */
    Object serialize(Column column, Object object);
}
