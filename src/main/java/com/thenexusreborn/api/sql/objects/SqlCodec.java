package com.thenexusreborn.api.sql.objects;

import com.thenexusreborn.api.sql.annotations.column.ColumnCodec;
import com.thenexusreborn.api.sql.annotations.column.ColumnType;

/**
 * This is a class to allow parsing of types outside of the default supported types.<br>
 * To use this class, simply create a class that implements this interface.<br>
 * Implement the logic for both the encode and decode methods<br>
 * Then simply annotation your field(s) using the {@link ColumnCodec} annotation. And that's it for this class<br>
 * If you would like to have default handling of a Class type, please use the {@link TypeHandler} class for that.<br>
 * Why Strings? Because the default type while using a codec is a VARCHAR on the MySQL Table. This makes it much easier to implement the backend. <br>
 * An {@link IllegalArgumentException} will be thrown if you try to use the {@link ColumnType} annotation that does not have a VARCHAR value. This is only used for setting the length of the column if you want to have it overriding the default length
 * @param <T> The Java type that this codec is for
 */
public interface SqlCodec<T> {
    /**
     * Encodes an object into a string<br>
     * The passed object should be safe to cast. It passes in the value in the Field of the Model Class
     * @param object The object to encode
     * @return The string form for this object. Please note: This is passed into the {@code decode} method
     */
    String encode(Object object);
    
    /**
     * Decodes the object from the provided String<br>
     * The string passed into this method is the same as the one returned from the {@code encode} method
     * @param encoded The encoded value
     * @return The decoded object
     */
    T decode(String encoded);
}
