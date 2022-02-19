package com.eztech.fitrans.constants;

public enum ExcelSection {
  CUSTOMER("Customer"),
  MODULE("Module"),
  COMPONENT("Component"),
  PROPERTY_VALUE("PropertyValue"),
  PROPERTY_SINGLE("PropertySingle"),
  PROPERTY("Property"),
  PROPERTY_RENDER_RELA("PropertyRenderRela"),
  ;

  final String typeValue;

  private ExcelSection(final String typeValue) {
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
