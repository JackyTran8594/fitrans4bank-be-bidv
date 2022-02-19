package com.eztech.fitrans.constants;

public enum FieldType {

  DOUBLE("Double"), //
  INTEGER("Integer"), //
  LONG("Long"), //
  STRING("String"), //
  DATE("Date");

  final String typeValue;

  private FieldType(final String typeValue) {
    this.typeValue = typeValue;
  }

  public String getName() {
    return name();
  }

  public String getValue() {
    return typeValue;
  }

  @Override
  public String toString() {
    return name();
  }

}
