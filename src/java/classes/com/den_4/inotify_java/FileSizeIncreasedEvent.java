package com.den_4.inotify_java;

public class FileSizeIncreasedEvent extends InotifyEvent {
    private long currentSize;
    private long delta;

    /**
     * @param watchDescriptor
     * @param mask
     * @param name
     */
    public FileSizeIncreasedEvent(int watchDescriptor, int mask, String name,
            final long currentSize, final long delta) {
        super(watchDescriptor, mask, name);
        this.currentSize = currentSize;
        this.delta = delta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FileSizeIncreasedEvent [currentSize=");
        builder.append(currentSize);
        builder.append(", delta=");
        builder.append(delta);
        builder.append("]");
        return builder.toString();
    }

}
