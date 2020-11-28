package dev.lajoscseppento.ruthless.demo.springbootlibrary;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = ItemRepositoryAutoConfiguration.class)
class ItemRepositoryAutoConfigurationTest {
  @Autowired private ApplicationContext applicationContext;

  @Test
  void test() {
    Object bean = applicationContext.getBean("itemRepository");
    assertThat(bean.getClass()).isEqualTo(ItemRepository.class);
  }
}
