package ru.brynkin.flightbooking.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

  private static final Properties PROPERTIES = new Properties();

  static {
    loadProperties();
  }

  private PropertiesUtil() {
  }

  public static String get(String key) {
    return PROPERTIES.getProperty(key);
  }

  private static void loadProperties() {
    try (InputStream resourceAsStream = PropertiesUtil.class.getClassLoader()
        .getResourceAsStream("application.properties")) {
      PROPERTIES.load(resourceAsStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
