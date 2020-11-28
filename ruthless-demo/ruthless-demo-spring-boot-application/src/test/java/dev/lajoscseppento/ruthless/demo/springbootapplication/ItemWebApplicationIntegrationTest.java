package dev.lajoscseppento.ruthless.demo.springbootapplication;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lajoscseppento.ruthless.demo.javalibrary.Item;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ItemWebApplicationIntegrationTest {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void test() throws Exception {
    // Given
    TimeUnit.SECONDS.sleep(10); // allow the server to generate some items

    // When
    Item[] items = restTemplate.getForObject("/", Item[].class);

    // Then
    assertThat(items).hasSizeGreaterThanOrEqualTo(2);
    assertThat(items[0].getName()).isEqualTo("Item 001");
  }
}
