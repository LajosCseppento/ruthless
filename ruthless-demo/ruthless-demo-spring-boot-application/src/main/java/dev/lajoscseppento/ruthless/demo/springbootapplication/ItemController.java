package dev.lajoscseppento.ruthless.demo.springbootapplication;

import dev.lajoscseppento.ruthless.demo.javalibrary.Item;
import dev.lajoscseppento.ruthless.demo.springbootlibrary.ItemRepository;
import java.util.Comparator;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {
  @Autowired private ItemRepository itemRepository;

  @GetMapping("/")
  public Stream<Item> index() {
    return itemRepository.getAll().stream().sorted(Comparator.comparing(Item::getName));
  }
}
