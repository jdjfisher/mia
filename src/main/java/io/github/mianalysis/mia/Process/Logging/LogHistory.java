package io.github.mianalysis.mia.Process.Logging;

import java.util.HashMap;

public class LogHistory implements LogRenderer {
    private HashMap<Level,Boolean> levelStatus = new HashMap<>();
    private static HashMap<Level,String> logHistory = new HashMap<>();

    public LogHistory() {
        levelStatus.put(Level.DEBUG,false);
        levelStatus.put(Level.ERROR,true);
        levelStatus.put(Level.MEMORY,false);
        levelStatus.put(Level.MESSAGE,false);
        levelStatus.put(Level.STATUS,false);
        levelStatus.put(Level.WARNING,false);

    }

    @Override
    public void write(String message, Level level) {
        // If this level isn't currently being written, skip it
        if (levelStatus.get(level) == null || !levelStatus.get(level))
            return;
        
        logHistory.put(level, logHistory.get(level) + message);
        
    }

    @Override
    public boolean isWriteEnabled(Level level) {
        return levelStatus.get(level);
    }

    @Override
    public void setWriteEnabled(Level level, boolean writeEnabled) {
        levelStatus.put(level, writeEnabled);
    }
    
    public String getLogHistory(Level level) {
        logHistory.putIfAbsent(level,"");
        return logHistory.get(level);
    }

    public void clearLogHistory() {
        logHistory.clear();
    }
}
