package com.thenexusreborn.api.sql.annotations.column;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows customization of the name of the column in the database. This annotation has the highest priority for naming columns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnName {
    String value();
}
