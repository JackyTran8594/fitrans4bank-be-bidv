package com.eztech.fitrans.locale;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom resource bundle
 *
 * @author HoangTD5
 * @version v1
 * @date 11/6/2020
 */
public class MultiResourceBundle extends ResourceBundle {

  private final List<ResourceBundle> delegates;

  public MultiResourceBundle(List<ResourceBundle> resourceBundles) {
    this.delegates = resourceBundles == null ? new ArrayList<>() : resourceBundles;
  }

  @Override
  protected Object handleGetObject(String key) {
    Optional<Object> firstPropertyValue = this.delegates.stream()
        .filter(delegate -> delegate != null && delegate.containsKey(key))
        .map(delegate -> delegate.getObject(key))
        .findFirst();

    return firstPropertyValue.orElse(null);
  }

  @Override
  public Enumeration<String> getKeys() {
    List<String> keys = this.delegates.stream()
        .filter(Objects::nonNull)
        .flatMap(delegate -> Collections.list(delegate.getKeys()).stream())
        .collect(Collectors.toList());

    return Collections.enumeration(keys);
  }
}
