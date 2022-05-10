package waltsai.colorfulpuff.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.apache.logging.log4j.LogManager;
import waltsai.colorfulpuff.Utils;
import waltsai.colorfulpuff.server.entity.AbstractPuffEntity;
import waltsai.colorfulpuff.server.entity.PuffEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    public static boolean BLINK_KEY = true;
    public static double ATTACK_DAMAGE_MULTIPLIER = 1.0;

    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("colorfulpuff.properties");

    static {
        try (BufferedReader reader = Files.newBufferedReader(CONFIG)) {
            Properties properties = new Properties();
            properties.load(reader);

            Config.BLINK_KEY = Boolean.parseBoolean(properties.getProperty("blink"));
            Config.ATTACK_DAMAGE_MULTIPLIER = Double.parseDouble(properties.getProperty("damage_multiplier"));
        } catch (Exception ignored) {
            save();
        }
    }

    static void save() {
        Properties properties = new Properties();
        properties.put("blink", String.valueOf(Config.BLINK_KEY));
        properties.put("damage_multiplier", String.valueOf(Config.ATTACK_DAMAGE_MULTIPLIER));

        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG)) {
            properties.store(writer, "Colorful Puff Mod Configuration");
        } catch (IOException exception) {
            LogManager.getLogger(Utils.class).error(exception.getMessage(), exception);
        }

        PuffEntity.ATTACKING_DAMAGE_BOOST = new EntityAttributeModifier(AbstractPuffEntity.ATTACKING_DAMAGE_BOOST_ID, "Attacking damage boost", (Config.ATTACK_DAMAGE_MULTIPLIER - 1), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
