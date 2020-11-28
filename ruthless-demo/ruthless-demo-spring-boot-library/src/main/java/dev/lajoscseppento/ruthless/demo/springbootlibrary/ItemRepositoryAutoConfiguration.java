package dev.lajoscseppento.ruthless.demo.springbootlibrary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ItemRepositoryAutoConfiguration {
  @Bean
  public ItemRepository itemRepository() {
    log.info("Creating item repository bean");
    return new ItemRepository();
  }
}
