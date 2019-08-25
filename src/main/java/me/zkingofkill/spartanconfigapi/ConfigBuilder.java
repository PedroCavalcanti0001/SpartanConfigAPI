package me.zkingofkill.spartanconfigapi;

import me.zkingofkill.spartanconfigapi.annotations.Config;
import me.zkingofkill.spartanconfigapi.annotations.ConfigValue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public abstract class ConfigBuilder {
    /**
     * @author Pedro Cavalcanti (zKingOfKill)
     * @version 1.0
     * @category Utils
     */
    public void init(Plugin plugin, Object object) {
        Class clazz = super.getClass();
        if (clazz.isAnnotationPresent(Config.class)) {
            Field[] fields = clazz.getDeclaredFields();

            String fileName = ((Config) clazz.getAnnotation(Config.class)).path();
            File file = new File(plugin.getDataFolder(), fileName);
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (Field field : fields) {
                if (field.isAnnotationPresent(ConfigValue.class)) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    String fieldpath = field.getAnnotation(ConfigValue.class).path();
                    Object fieldvalue = null;
                    boolean translateAlternativeColors = field.getAnnotation(ConfigValue.class).translateAlternativeColors();
                    try {
                        fieldvalue = field.get(object);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (!fileConfiguration.contains(fieldpath)) {
                        fileConfiguration.set(fieldpath, fieldvalue);
                        try {
                            fileConfiguration.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Object value = fileConfiguration.get(fieldpath);
                        if (translateAlternativeColors) {
                            if (value instanceof String) {
                                value = ((String) fieldvalue).replace("&", "§");
                            }

                        }
                        field.set(object, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

        }

    }
}
