package dev.lajoscseppento.ruthless.demo.springbootapplication;

import dev.lajoscseppento.ruthless.demo.javalibrary.Item;
import dev.lajoscseppento.ruthless.demo.springbootlibrary.ItemRepository;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ItemController {
  private final ItemRepository itemRepository;

  @GetMapping("/")
  public Stream<Item> index() {
    return itemRepository.getAll().stream().sorted(Comparator.comparing(Item::getName));
  }
}
