package dev.lajoscseppento.ruthless.demo.springbootlibrary;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import dev.lajoscseppento.ruthless.demo.javalibrary.Item;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ItemRepository {
  private final Set<Item> items = Sets.newConcurrentHashSet();

  @PostConstruct
  private void init() {
    log.info("Initialising item repository");
  }

  public void add(@NonNull Item item) {
    log.info("Add item: {}", item);
    items.add(item);
  }

  public ImmutableSet<Item> getAll() {
    return ImmutableSet.copyOf(items);
  }
}
