package com.eztech.fitrans.locale;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.stream.Collectors;

/**
 * Custom control resource bundle
 *
 * @author HoangTD5
 * @version v1
 * @date 11/6/2020
 */
public class MultiResourceBundleControl extends Control {

  private final String baseName;
  private final String[] dependentBaseNames;

  public MultiResourceBundleControl(String baseName, String... dependentBaseNames) {
    this.baseName = baseName;
    this.dependentBaseNames = dependentBaseNames;
  }

  public String getBaseName() {
    return this.baseName;
  }

  @Override
  public ResourceBundle newBundle(String baseName, Locale locale, String format,
      ClassLoader loader, boolean reload) {
    List<ResourceBundle> delegates = Arrays.stream(this.dependentBaseNames)
        .filter(currentBaseName -> currentBaseName != null && !"".equals(currentBaseName.trim()))
        .map(currentBaseName -> ResourceBundle.getBundle(currentBaseName, locale))
        .collect(Collectors.toList());

    return new MultiResourceBundle(delegates);
  }
}
