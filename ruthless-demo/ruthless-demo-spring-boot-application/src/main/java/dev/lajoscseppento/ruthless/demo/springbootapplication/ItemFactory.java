package dev.lajoscseppento.ruthless.demo.springbootapplication;

import dev.lajoscseppento.ruthless.demo.javalibrary.Item;
import dev.lajoscseppento.ruthless.demo.springbootlibrary.ItemRepository;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@EnableScheduling
@Slf4j
public class ItemFactory {
  private final AtomicLong nextId = new AtomicLong(1);
  private final ItemRepository itemRepository;

  @Scheduled(fixedRate = 5000)
  void generateItem() {
    long id = nextId.getAndIncrement();
    Item item =
        Item.builder()
            .name(String.format("Item %03d", id))
            .description("Item generated at " + ZonedDateTime.now())
            .build();
    log.info("Generate item: {}", item);
    itemRepository.add(item);
  }
}
