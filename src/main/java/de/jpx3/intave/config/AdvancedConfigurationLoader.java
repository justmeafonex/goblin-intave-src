package de.jpx3.intave.config;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.resource.Resource;
import de.jpx3.intave.resource.Resources;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Loads check tuning from {@code checks.yml} (formerly {@code advanced.yml}).
 * <p>
 * checks.yml is written to the data folder on first run and is then the live,
 * directly-editable source of truth for check thresholds/tuning - no toggle in
 * config.yml is required to "enable" it.
 * <p>
 * On every load, the general settings that belong in config.yml (prefix, verbose/notify
 * layout, kick/ban commands, physics mitigation, rejoin delay, cloud privacy) are re-synced
 * from config.yml into checks.yml via {@link SimpleToAdvancedConfigConverter}, so config.yml
 * remains the place to edit those, while checks.yml keeps any custom check tuning intact.
 */
public final class AdvancedConfigurationLoader implements ConfigurationLoader {
  private static final String CHECKS_FILE_NAME = "checks.yml";

  private final IntavePlugin plugin;

  public AdvancedConfigurationLoader(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public YamlConfiguration fetchConfiguration() {
    Resource simpleConfig = Resources.resourceFromFile(new File(plugin.dataFolder(), "config.yml"));
    Resource simpleConfigInClasspath = Resources.resourceFromJarOrBuild("config.yml");
    if (!simpleConfig.available()) {
      try (InputStream read = simpleConfigInClasspath.read()) {
        simpleConfig.write(read);
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
      simpleConfig = simpleConfigInClasspath;
    }

    Resource checksConfig = Resources.resourceFromFile(new File(plugin.dataFolder(), CHECKS_FILE_NAME));
    if (!checksConfig.available()) {
      Resource checksConfigInClasspath = Resources.resourceFromJarOrBuild(CHECKS_FILE_NAME);
      try (InputStream read = checksConfigInClasspath.read()) {
        checksConfig.write(read);
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
    }

    // Re-apply config.yml's prefix/messages/kick-ban-commands/etc. every load, so config.yml
    // stays authoritative for those without requiring checks.yml to be regenerated.
    Resource conversionData = Resources.resourceFromJarOrBuild("ctvs.mx");
    SimpleToAdvancedConfigConverter converter = new SimpleToAdvancedConfigConverter(
      simpleConfig, checksConfig, conversionData
    );
    converter.convert();

    return YamlConfiguration.loadConfiguration(new InputStreamReader(checksConfig.read()));
  }
}
