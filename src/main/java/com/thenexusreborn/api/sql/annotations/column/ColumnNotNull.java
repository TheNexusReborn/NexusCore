package com.thenexusreborn.api.sql.annotations.column;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation tells the library that the column cannot be null. Please note, this will cause SQLExceptions if there is a null value for a field on insert (or update)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnNotNull {
    
}
