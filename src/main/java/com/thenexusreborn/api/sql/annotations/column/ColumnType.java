package com.thenexusreborn.api.sql.annotations.column;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows customization of the MySQL type. <br>
 * Please be aware that if not handled correctly, things can fail to process and throw Exceptions. The project that this is taken from mainly uses it to customize varchar types with an SqlCodec
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnType {
    String value();
}
