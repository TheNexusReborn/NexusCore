package com.thenexusreborn.api.sql.objects;

import com.thenexusreborn.api.sql.objects.typehandlers.PropertyTypeHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a Row or a Record in a Table<br>
 * This is mainly to allow closing the connection to the database as quick as possible as it stores the data in a HashMap based on the column<br>
 * The values from each database cell are parsed using {@link TypeHandler}'s for the field/class and any defined {@link SqlCodec}'s for the columns<br>
 * This means that you can case the value from the {@code getObject} method to what you know the type to be.<br>
 * You can also use the other methods to do this for you, they will attempt to parse the values as well depending on the type of value<br>
 * Please see each individual method for more information
 */
public class Row {
    private final Map<String, Object> data = new HashMap<>();
    
    public Row(ResultSet rs, SQLDatabase database) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            
            Table table = database.getTable(metaData.getTableName(1));
            
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i).toLowerCase();
                Column column = table.getColumn(columnName);
                if (column == null) {
                    continue;
                }
                
                try {
                    if (rs.getObject(i) == null) {
                        this.data.put(columnName, null);
                    } else if (column.getTypeHandler() instanceof PropertyTypeHandler) {
                        this.data.put(columnName, rs.getObject(i));
                    } else if (column.getCodec() != null) {
                        data.put(columnName, column.getCodec().decode(rs.getString(i)));
                    } else if (column.getTypeHandler() != null) {
                        Object object = column.getTypeHandler().getDeserializer().deserialize(column, rs.getObject(i));
                        this.data.put(columnName, object);
                    } else {
                        this.data.put(columnName, rs.getObject(i));
                    }
                } catch (Throwable t) {
//                    System.err.println("Error while parsing row from " + table.getName() + '.' + columnName + ": " + t.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the Object based on a key
     *
     * @param key The column name
     * @return The object in the column
     */
    public Object getObject(String key) {
        return data.get(key.toLowerCase());
    }
    
    /**
     * Gets the object as a string<br>
     * This method either casts to a String or calls the toString() method on the object. <br>
     * If the value is null, then it will return "null" as a String
     *
     * @param key The column name
     * @return The object in the column as a string
     */
    public String getString(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof String str) {
            return str;
        }
        if (value == null) {
            return "null";
        } else {
            return value.toString();
        }
    }
    
    /**
     * Gets the object as an int<br>
     * This method will parse the object into an Integer if it is a String<br>
     *
     * @param key The column name
     * @return The object in the column as an int
     */
    public int getInt(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Integer) {
            return (int) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return 0;
    }
    
    /**
     * Gets the object as a long
     *
     * @param key The column name
     * @return The object in the column as a long
     */
    public long getLong(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Long) {
            return (long) value;
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        return 0;
    }
    
    /**
     * Gets the object as a double
     *
     * @param key The column name
     * @return The object in the column as a double
     */
    public double getDouble(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof String) {
            return Double.parseDouble((String) value);
        }
        return 0;
    }
    
    /**
     * Gets the object as a float
     *
     * @param key The column name
     * @return The object in the column as a float
     */
    public float getFloat(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Float) {
            return (float) value;
        } else if (value instanceof String) {
            return Float.parseFloat((String) value);
        }
        return 0;
    }
    
    /**
     * Gets the object as a boolean
     *
     * @param key The column name
     * @return The object in the column as a boolean
     */
    public boolean getBoolean(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof Boolean) {
            return (boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Integer) {
            return (int) value == 1;
        } else if (value instanceof Long) {
            return (long) value == 1;
        }
        return false;
    }
    
    public UUID getUuid(String key) {
        Object value = data.get(key.toLowerCase());
        if (value instanceof UUID uuid) {
            return uuid;
        } else if (value instanceof String str) {
            return UUID.fromString(str);
        }
        return null;
    }
    
    /**
     * Gets the object from a codec
     *
     * @param key The column name
     * @return The object in the column from the codec
     */
    public <T> T get(String key, SqlCodec<T> codec) {
        Object value = data.get(key.toLowerCase());
        return codec.decode((String) value);
    }
    
    /**
     * @return All stored data from this row. The returned Map is a copy of the original map. Changes to the returned map does not reflect the stored Map
     */
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }
}