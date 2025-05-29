package com.thenexusreborn.api.rank;

public interface IRank {
    String name(); //enum method
    String getBaseColor();
    String getPrefix();
    String getChatColor();
    boolean isNameBold();
    String[] getDeclaredPermissions();
    String[] getAllPermissions();
    IRank[] getParentRanks();
}