package de.jpx3.intave.block.variant;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.stream.Collectors;

final class EnumSetting extends NamedSetting<String> {
  private final ImmutableSet<String> values;
  private final List<String> valuesAsList;
  private final Class<?> owner;

  EnumSetting(String name, Class<?> owner, Collection<?> values) {
    super(name, String.class);
    if (!owner.isEnum()) {
      throw new IllegalStateException("Not an enum");
    }
    this.owner = owner;
    this.values = ImmutableSet.copyOf(values.stream().map(value -> ((Enum<?>) value).name()).collect(Collectors.toList()));
    this.valuesAsList = new ArrayList<>(this.values);
  }

  public <K extends Enum<K>> K enumType(Class<K> enumClass, String name) {
    return Enum.valueOf(enumClass, name.toUpperCase(Locale.ROOT));
  }

  @Override
  public int indexOf(String value) {
    return valuesAsList.indexOf(value);
  }

  @Override
  public Collection<String> values() {
    return values;
  }

  @Override
  public Optional<String> findByName(String name) {
    if (values.contains(name)) {
      return Optional.of(name);
    }
    return Optional.empty();
  }
}
