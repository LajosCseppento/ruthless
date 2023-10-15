package dev.lajoscseppento.ruthless.demo.springbootapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Spring Boot application entry point. */
@SpringBootApplication
public class ItemWebApplication {
  /**
   * Application entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ItemWebApplication.class, args);
  }
}
