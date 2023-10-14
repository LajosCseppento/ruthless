package dev.lajoscseppento.ruthless.demo.springbootlibrary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Auto-configuration for {@link ItemRepository}. */
@Configuration
@Slf4j
public class ItemRepositoryAutoConfiguration {
  /**
   * Creates an item repository bean.
   *
   * @return the item repository bean
   */
  @Bean
  public ItemRepository itemRepository() {
    log.info("Creating item repository bean");
    return new ItemRepository();
  }
}
