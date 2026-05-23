package com.thenexusreborn.api.rank;

import com.thenexusreborn.api.format.NameFormat;
import com.thenexusreborn.api.format.PrefixFormat;

import java.util.Set;

public interface IRank {
    String name(); //enum method
    String getBaseColor();
    String getPrefix();
    String getChatColor();
    
    PrefixFormat getPrefixFormat();
    NameFormat getNameFormat();
    
    Set<String> getDeclaredPermissions();
    
    IRank[] getParentRanks();
}