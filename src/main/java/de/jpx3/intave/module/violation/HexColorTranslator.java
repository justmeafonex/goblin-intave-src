package de.jpx3.intave.module.violation;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates {@code &#RRGGBB} hex color sequences (in addition to Bukkit's standard
 * legacy {@code &0}-{@code &f}/{@code &k}-{@code &r} codes) into the section-symbol
 * escape sequence Minecraft clients understand, so hex colors can be used anywhere
 * message text can - prefix, verbose/notify layout in config.yml, and commands such
 * as {@code /intave internals sendnotify}.
 * <p>
 * Run this BEFORE {@link org.bukkit.ChatColor#translateAlternateColorCodes}, since
 * the produced escape sequence contains no remaining {@code &} characters for it to
 * touch.
 */
public final class HexColorTranslator {
  private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

  private HexColorTranslator() {
  }

  public static String translate(String input) {
    if (input == null || input.indexOf('#') < 0) {
      return input;
    }
    Matcher matcher = HEX_PATTERN.matcher(input);
    StringBuilder result = new StringBuilder(input.length());
    while (matcher.find()) {
      String hex = matcher.group(1);
      String legacySequence = ChatColor.of("#" + hex).toString();
      matcher.appendReplacement(result, Matcher.quoteReplacement(legacySequence));
    }
    matcher.appendTail(result);
    return result.toString();
  }
}
