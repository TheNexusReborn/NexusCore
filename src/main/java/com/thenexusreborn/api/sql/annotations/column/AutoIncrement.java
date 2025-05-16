package com.thenexusreborn.api.sql.annotations.column;

import com.thenexusreborn.api.sql.objects.Table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This sets the field to be an auto-increment field<br>
 * Please see the {@link Table} class for more information on how to setup a table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoIncrement {
    
}
