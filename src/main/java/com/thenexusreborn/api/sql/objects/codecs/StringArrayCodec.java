package com.thenexusreborn.api.sql.objects.codecs;

import com.thenexusreborn.api.sql.objects.SqlCodec;

public class StringArrayCodec implements SqlCodec<String[]> {
    @Override
    public String encode(Object object) {
        String[] value = (String[]) object;
        StringBuilder sb = new StringBuilder();
        for (String s : value) {
            sb.append(s).append(",");
        }
        
        return sb.substring(0, sb.length() - 1);
    }
    
    @Override
    public String[] decode(String encoded) {
        return encoded.split(",");
    }
}
