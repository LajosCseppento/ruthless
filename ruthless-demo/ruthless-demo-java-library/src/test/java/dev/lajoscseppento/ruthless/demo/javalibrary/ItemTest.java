package dev.lajoscseppento.ruthless.demo.javalibrary;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ItemTest {
  @Test
  void test() {
    // Given
    Item item = Item.builder().name("Name").description("Description").build();

    // When
    String itemStr = item.toString();

    // Then
    assertThat(itemStr).isEqualTo("Item(name=Name, description=Description)");
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
