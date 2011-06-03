package com.googlecode.arcane;


import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 *
 */
public class HelloWorld {
  public static void main(String[] args) {
      Properties properties = new Properties();
      try {
      String path = HelloWorld.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      path=path.substring(0, path.lastIndexOf('/')+1);
      FileInputStream propertiesFileStream = new FileInputStream(path + "helloworld.properties");
      properties.load(propertiesFileStream);

          propertiesFileStream.close();
      } catch (IOException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch (URISyntaxException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      JFrame frame = new JFrame("HelloWorld");
    final JLabel label = new JLabel(properties.getProperty("text"));
    frame.getContentPane().add(label);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}