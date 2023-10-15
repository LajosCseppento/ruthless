package dev.lajoscseppento.ruthless.demo.springbootlibrary;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import dev.lajoscseppento.ruthless.demo.javalibrary.Item;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/** Simple repository. */
@Repository
@Slf4j
public class ItemRepository {
  private final Set<Item> items = Sets.newConcurrentHashSet();

  /** Initialises the repository. */
  @PostConstruct
  private void init() {
    log.info("Initialising item repository");
  }

  /**
   * Adds an item.
   *
   * @param item the item to add
   */
  public void add(@NonNull Item item) {
    log.info("Add item: {}", item);
    items.add(item);
  }

  /**
   * Returns all items.
   *
   * @return all items
   */
  public ImmutableSet<Item> getAll() {
    return ImmutableSet.copyOf(items);
  }
}
