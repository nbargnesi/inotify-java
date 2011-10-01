package com.den_4.inotify_java;

public interface FluctuatorListener extends InotifyEventListener {

    public abstract void fileSizeIncreased(final FileSizeIncreasedEvent e); 
    
    public abstract void fileSizeDecreased(final FileSizeDecreasedEvent e);
    
}
