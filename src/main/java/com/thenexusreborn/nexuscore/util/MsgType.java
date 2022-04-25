package com.thenexusreborn.nexuscore.util;

public enum MsgType {
    INFO("&6&l>>&e "),
    SUCCESS("&6&l>>&a "),
    IMPORTANT("&2&l>>&a "),
    WARN("&6&l>>&c "),
    ERROR("&4&l>>&7 "),
    SEVERE("&4&l>>&4 "),
    DETAIL("&d&l>>&7 "),
    VERBOSE("&6&l>>&7&o ");

    private final String prefix;

    MsgType(String prefix){
        this.prefix = prefix;
    }

    public String getPrefix(){
        return prefix;
    }


    @Override
    public String toString(){
        return prefix;
    }
}
