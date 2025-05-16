package com.thenexusreborn.api.sql.annotations.table;

import java.lang.annotation.*;

/**
 * This annotation allows customization of the Table name<br>
 * This annotation has the highest priority for names in a table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface TableName {
    String value();
}
