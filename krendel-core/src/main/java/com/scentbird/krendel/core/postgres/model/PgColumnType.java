package com.scentbird.krendel.core.postgres.model;

import java.util.Objects;

public class PgColumnType {

    private String dataType;
    private String udtName;
    private Integer characterMaximumLength;
    private Integer numericPrecision;
    private Integer numericScale;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUdtName() {
        return udtName;
    }

    public void setUdtName(String udtName) {
        this.udtName = udtName;
    }

    public Integer getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public void setCharacterMaximumLength(Integer characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    public Integer getNumericPrecision() {
        return numericPrecision;
    }

    public void setNumericPrecision(Integer numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    public Integer getNumericScale() {
        return numericScale;
    }

    public void setNumericScale(Integer numericScale) {
        this.numericScale = numericScale;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, udtName, characterMaximumLength, numericPrecision, numericScale);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PgColumnType) {
            PgColumnType other = (PgColumnType) obj;
            return Objects.equals(dataType, other.dataType)
                    && Objects.equals(udtName, other.udtName)
                    && Objects.equals(characterMaximumLength, other.characterMaximumLength)
                    && Objects.equals(numericPrecision, other.numericPrecision)
                    && Objects.equals(numericScale, other.numericScale);
        } else {
            return false;
        }
    }

}
