package dev.lajoscseppento.ruthless.demo.javalibrary;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Values;

class ItemTest {
  @CartesianTest
  void test(
      @Values(strings = {"Name #1", "Name #2"}) String name,
      @Values(strings = {"Description #1", "Description #2"}) String description) {
    // Given
    Item item = Item.builder().name(name).description(description).build();

    // When
    String itemStr = item.toString();

    // Then
    assertThat(itemStr)
        .isEqualTo(String.format("Item(name=%s, description=%s)", name, description));
  }

  @Test
  void testSerialisation() throws Exception {
    // Given
    Item item = Item.builder().name("Name").description("Description").build();
    ObjectMapper mapper = new ObjectMapper();

    // When
    String itemJson = mapper.writeValueAsString(item);
    Item itemCopy = mapper.readValue(itemJson, Item.class);

    // Then
    assertThat(itemCopy).isEqualTo(item).isNotSameAs(item);
  }
}
