package dev.lajoscseppento.ruthless.demo.javapplication;

import dev.lajoscseppento.ruthless.demo.javalibrary.Item;

/** Application entry point. */
public class ItemApplication {
  /**
   * Application entry point.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Item item = Item.builder().name("Name").description("Description").build();
    System.out.println(item);
  }
}
