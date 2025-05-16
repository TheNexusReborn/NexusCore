package com.thenexusreborn.api.sql.objects;

import com.thenexusreborn.api.sql.DatabaseRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This allows the abiltity to have Java Classes converted automatically to MySQL Types and vica versa<br>
 * It is up to the implementor to handle the seriazation and deseralization<br>
 * You can add these handlers to a {@link DatabaseRegistry} which will allow it to be used by all databases registered under that Registry<br>
 * Alternatively, you can add the handlers to {@link SQLDatabase} of which it will only be used by tables in that database.<br>
 * You cannot add them to {@link Table}'s individually. Use a {@link SqlCodec} if you want to do that. 
 */
public class TypeHandler {
    protected final Class<?> mainClass;
    protected final Set<Class<?>> additionalClasses = new HashSet<>();
    protected final String mysqlType;
    
    protected final TypeSerializer serializer;
    protected final TypeDeserializer deserializer;
    
    public TypeHandler(Class<?> mainClass, String mysqlType, TypeSerializer serializer, TypeDeserializer deserializer) {
        this.mainClass = mainClass;
        this.mysqlType = mysqlType;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }
    
    public TypeSerializer getSerializer() {
        return serializer;
    }
    
    public TypeDeserializer getDeserializer() {
        return deserializer;
    }
    
    public void addAdditionalClass(Class<?>... classes) {
        if (classes != null) {
            this.additionalClasses.addAll(List.of(classes));
        }
    }
    
    public Class<?> getMainClass() {
        return mainClass;
    }
    
    public Set<Class<?>> getAdditionalClasses() {
        return additionalClasses;
    }
    
    public String getMysqlType() {
        return mysqlType;
    }
    
    public boolean matches(Class<?> clazz) {
        if (this.mainClass.equals(clazz)) {
            return true;
        }
    
        for (Class<?> additionalClass : this.additionalClasses) {
            if (additionalClass.equals(clazz)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeHandler that = (TypeHandler) o;
        return Objects.equals(mainClass, that.mainClass) && Objects.equals(additionalClasses, that.additionalClasses);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mainClass, additionalClasses);
    }
}
