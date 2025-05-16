package com.thenexusreborn.api.sql.annotations.table;

import com.thenexusreborn.api.sql.objects.ObjectHandler;

import java.lang.annotation.*;

/**
 * This annotation tells the library to use an {@link ObjectHandler} for the table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface TableHandler {
    Class<? extends ObjectHandler> value();
}
