package dev.lajoscseppento.ruthless.demo.javapplication;

import dev.lajoscseppento.ruthless.demo.javalibrary.Item;

public class ItemApplication {
  public static void main(String[] args) {
    Item item = Item.builder().name("Name").description("Description").build();
    System.out.println(item);
  }
}
