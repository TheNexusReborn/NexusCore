package com.thenexusreborn.api.sql.objects;

import com.stardevllc.observable.Property;
import com.thenexusreborn.api.sql.annotations.ID;
import com.thenexusreborn.api.sql.annotations.column.*;
import com.thenexusreborn.api.sql.objects.typehandlers.PropertyTypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This represents a column in a Table within a Database. Instances of this class are created for every field that is not ignored following the ignoring rules in the {@link Table} documentation<br>
 * This class is mainly an internal class used by the library. You shouldn't need to interact with this class directly, however you can find how this works for how Columns work in this library<br>
 * When determining the type of a field, this library first looks at the {@link ColumnCodec} and set it to a default of varchar(1000), this can be overridden with the {@link ColumnType} annotation<br>
 * It will then look at to see if there is a registered table in the database. if there is, then it set the values based on the primary key column of the table<br>
 * Then it will look at the {@link ColumnType} annotation for an override. Please note, these must be compatible. <br>
 * For compatibility, it can just be default compatible, using a {@link TypeHandler} or {@link SqlCodec}. <br>
 * The name of the column can be taken from the name of the class, which will be all lower-case, or using the {@link ColumnName} annotation.<br>
 * You can use the other annotatations to customize the columns as you see fit. This library will not check to see if your configuration is wrong, you will get SQLExceptions if it is.
 */
public class Column implements Comparable<Column> {
    private final Table table;
    private final Field field;
    
    private String name, type;
    private boolean primaryKey, autoIncrement, notNull;
    private SqlCodec<?> codec;
    private Table linkedTable;
    private TypeHandler typeHandler;
    
    /**
     * Constructs a column based on a class and the Field
     *
     * @param table The class
     * @param field The field
     */
    public Column(Table table, Field field) {
        this.table = table;
        this.field = field;
        
        if (field.isAnnotationPresent(ColumnCodec.class)) {
            try {
                codec = field.getAnnotation(ColumnCodec.class).value().getConstructor().newInstance();
                type = "varchar(1000)";
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            Table fieldTable = table.getDatabase().getTable(field.getType());
            if (fieldTable != null) {
                this.linkedTable = fieldTable;
                this.type = fieldTable.getPrimaryKeyColumn().getType();
                this.typeHandler = fieldTable.getPrimaryKeyColumn().getTypeHandler();
            }
        }
        
        if (field.isAnnotationPresent(ColumnName.class)) {
            name = field.getAnnotation(ColumnName.class).value();
        }
        
        if (Property.class.isAssignableFrom(field.getType())) {
            type = "varchar(1000)";
            typeHandler = new PropertyTypeHandler();
        }
        
        primaryKey = field.isAnnotationPresent(PrimaryKey.class);
        autoIncrement = field.isAnnotationPresent(AutoIncrement.class);
        notNull = field.isAnnotationPresent(ColumnNotNull.class);
        
        if (field.isAnnotationPresent(ID.class)) {
            primaryKey = true;
            autoIncrement = true;
        }
        
        boolean fieldIsLong = field.getType().equals(long.class) || field.getType().equals(Long.class);
        boolean fieldIsId = field.getName().equalsIgnoreCase("id");
        if (fieldIsLong && fieldIsId) {
            autoIncrement = true;
            primaryKey = true;
        }
        
        if (name == null || name.isEmpty()) {
            name = field.getName().toLowerCase();
        }
        
        if (codec == null && typeHandler == null) {
            for (TypeHandler typeHandler : this.table.getDatabase().getTypeHandlers()) {
                if (typeHandler.matches(this.field.getType())) {
                    this.typeHandler = typeHandler;
                    break;
                }
            }
            
            if (this.typeHandler != null) {
                type = this.typeHandler.getMysqlType();
            } else {
                throw new IllegalArgumentException("Could not determine a handler or a codec for the field type " + field.getType().getName() + " in class " + table.getModelClass().getName());
            }
        }
        
        if (field.isAnnotationPresent(ColumnType.class)) {
            type = field.getAnnotation(ColumnType.class).value();
        }
        
        Class<?> fieldType = field.getType();
        
        if (String.class.equals(fieldType) || char.class.equals(fieldType) || Character.class.equals(fieldType) || boolean.class.equals(fieldType)
                || Boolean.class.equals(fieldType) || UUID.class.equals(fieldType) || Enum.class.isAssignableFrom(fieldType)) {
            autoIncrement = false;
        }
    }
    
    public Logger getLogger() {
        return table.getLogger();
    }
    
    public TypeHandler getTypeHandler() {
        return typeHandler;
    }
    
    public Table getLinkedTable() {
        return linkedTable;
    }
    
    public Table getTable() {
        return table;
    }
    
    public Field getField() {
        return field;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    
    public boolean isAutoIncrement() {
        return autoIncrement;
    }
    
    public boolean isNotNull() {
        return notNull;
    }
    
    public SqlCodec<?> getCodec() {
        return codec;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Column column = (Column) o;
        return Objects.equals(name, column.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public int compareTo(Column other) {
        if (this.primaryKey) {
            return 1;
        }
        
        if (other.primaryKey) {
            return -1;
        }
        
        return name.compareTo(other.name);
    }
}