package dev.lajoscseppento.ruthless.demo.springbootlibrary;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import dev.lajoscseppento.ruthless.demo.javalibrary.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ItemRepository.class)
class ItemRepositoryTest {
  @Autowired private ItemRepository itemRepository;

  @Test
  void test() {
    // Given
    Item item1 = Item.builder().name("1").description("First").build();
    Item item2 = Item.builder().name("2").description("Second").build();

    // When
    itemRepository.add(item1);
    itemRepository.add(item2);

    // Then
    ImmutableSet<Item> items = itemRepository.getAll();
    assertThat(items).containsExactlyInAnyOrder(item1, item2);
  }
}
