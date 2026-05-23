package com.thenexusreborn.api.format;

import com.stardevllc.starlib.objects.builder.AbstractBuilder;

/**
 * Format class for player names
 */
public record NameFormat(String color, boolean bold, boolean italics) {
    
    public String format(String str) {
        return color + (bold ? "&l" : "") + (italics ? "&o" : "") + str;
    }
    
    public static class Builder extends AbstractBuilder<NameFormat, Builder> {
        
        private String color;
        private boolean bold, italics;
        
        public Builder() {}
        
        public Builder(Builder builder) {
            this.color = builder.color;
            this.bold = builder.bold;
            this.italics = builder.italics;
        }
        
        public Builder color(String color) {
            this.color = color;
            return self();
        }
        
        public Builder bold() {
            this.bold = true;
            return self();
        }
        
        public Builder notBold() {
            this.bold = false;
            return self();
        }
        
        public Builder italics() {
            this.italics = true;
            return self();
        }
        
        public Builder notItalics() {
            this.italics = false;
            return self();
        }
        
        @Override
        public NameFormat build() {
            return new NameFormat(color, bold, italics);
        }
        
        @Override
        public Builder clone() {
            return new Builder(this);
        }
    }
}