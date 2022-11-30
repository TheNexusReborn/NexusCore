package com.thenexusreborn.nexuscore.util;

import com.thenexusreborn.api.util.TimeUnit;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 0 or # to represent the didgit and then use any alias in the TimeUnit (nexus) class to signify the type. This is with the % operator in front 
 * and behind the format. Anything else is ignored
 * For the 0 or # ones, please follow the rules for the Java Decimal Format class
 * Example: %00seconds%s with given input in milliseconds 5000 will display 05s
 */
public class TimeFormat {
    
    private static final Comparator<TimeUnit> UNIT_COMPARATOR = (o1, o2) -> {
        if (o1.ordinal() < o2.ordinal()) {
            return 1;
        } else if (o1.ordinal() == o2.ordinal()) {
            return 0;
        }
        return -1;
    };
    
    private static final TreeSet<TimeUnit> UNIT_ORDER = new TreeSet<>(UNIT_COMPARATOR);
    
    static {
        UNIT_ORDER.addAll(Arrays.asList(TimeUnit.values()));
        UNIT_ORDER.remove(TimeUnit.UNDEFINED);
    }
    
    private String pattern;
    
    private Map<TimeUnit, TimePattern> unitPatterns = new HashMap<>();
    
    public TimeFormat(String pattern) {
        this.pattern = pattern;
        parsePattern();
    }
    
    public String format(long time) {
        String formattedTime = this.pattern;
        long totalTime = time;
        for (TimeUnit unit : UNIT_ORDER) {
            if (!unitPatterns.containsKey(unit)) {
                continue;
            }
    
            long unitLength = unit.convertTime(totalTime);
            totalTime -= unit.toMilliseconds(unitLength);
    
            TimePattern timePattern = unitPatterns.get(unit);
            String value = new DecimalFormat(timePattern.timePattern()).format(unitLength);
            formattedTime = formattedTime.replace("%" + timePattern.timePattern() + timePattern.unitPattern() + "%", value);
        }
        
        return formattedTime;
    }
    
    public void setPattern(String pattern) {
        this.unitPatterns.clear();
        this.pattern = pattern;
        parsePattern();
    }
    
    private void parsePattern() {
        int startIndex = -1, endIndex = -1;
        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder unitBuilder = new StringBuilder();
    
        for (int i = 0; i < this.pattern.length(); i++) {
            char c = this.pattern.charAt(i);
            if (c == '%') {
                if (startIndex == -1) {
                    startIndex = i;
                    continue;
                } else if (endIndex == -1) {
                    endIndex = i;
                }
            }
        
            if (startIndex != -1 && endIndex != -1) {
                TimeUnit unit = TimeUnit.matchUnit(unitBuilder.toString());
                if (unit == null || unit == TimeUnit.UNDEFINED) {
                    continue;
                }
            
                unitPatterns.put(unit, new TimePattern(unitBuilder.toString(), patternBuilder.toString()));
                startIndex = -1;
                endIndex = -1;
                patternBuilder = new StringBuilder();
                unitBuilder = new StringBuilder();
            } else if (startIndex != -1 && endIndex == -1) {
                if (c == '0' || c == '#') {
                    patternBuilder.append(c);
                } else {
                    unitBuilder.append(c);
                }
            }
        }
    }
    
    record TimePattern(String unitPattern, String timePattern) {}
}
