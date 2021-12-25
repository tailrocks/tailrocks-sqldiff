package com.tailrocks.sqldiff.core.postgres.model;

import java.util.Objects;

public class PgSequence implements PgElement {

    private String name;

    private String dataType;

    private long startValue;
    private long minValue;
    private long maxValue;
    private long incrementBy;
    private long cacheSize;

    private String depType;

    private PgColumn owner;

    public PgSequence(String name, String dataType, long startValue, long minValue, long maxValue, long incrementBy,
                      long cacheSize) {
        this.name = name;
        this.dataType = dataType;
        this.startValue = startValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.incrementBy = incrementBy;
        this.cacheSize = cacheSize;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public long getStartValue() {
        return startValue;
    }

    public long getMinValue() {
        return minValue;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public long getIncrementBy() {
        return incrementBy;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public String getDepType() {
        return depType;
    }

    public void setDepType(String depType) {
        this.depType = depType;
    }

    public PgColumn getOwner() {
        return owner;
    }

    public void setOwner(PgColumn owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dataType, startValue, minValue, maxValue, incrementBy, cacheSize, depType);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PgSequence) {
            PgSequence other = (PgSequence) obj;
            return Objects.equals(name, other.name)
                    && Objects.equals(dataType, other.dataType)
                    && Objects.equals(startValue, other.startValue)
                    && Objects.equals(minValue, other.minValue)
                    && Objects.equals(maxValue, other.maxValue)
                    && Objects.equals(incrementBy, other.incrementBy)
                    && Objects.equals(cacheSize, other.cacheSize)
                    && Objects.equals(depType, other.depType);
        } else {
            return false;
        }
    }

}
