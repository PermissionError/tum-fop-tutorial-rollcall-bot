package dev.rayma;

import dev.rayma.commands.RollcallCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Launcher {
    public static final Logger LOGGER = LoggerFactory.getLogger("Rollcall App");
    public static JDA JDA;

    public static Properties config = new Properties();
    public static void main(String[] args) throws InterruptedException {
        try (InputStream is = new FileInputStream("config.properties")) {
            config.load(is);
        } catch (FileNotFoundException ex) {
            LOGGER.error("Config file (config.properties) not found!");
            System.exit(1);
        } catch (IOException ex) {
            LOGGER.error("Unknown IOException occurred while reading config.properties.");
            System.exit(1);
        }
        JDABuilder builder = JDABuilder.createDefault(config.getProperty("token"), GatewayIntent.GUILD_MEMBERS);
        builder.setActivity(Activity.watching("over you"));

        builder.addEventListeners(new RollcallCommand());

        JDA = builder.build().awaitReady();

        Guild tutorialServer = JDA.getGuildById(config.getProperty("tutorial-server-id"));

        if(tutorialServer == null) {
            LOGGER.error("Tutorial Server not found.");
            System.exit(1);
        }

        tutorialServer.updateCommands()
                .addCommands(
                        Commands.slash("rollcall", "Initiates a rollcall")
                                .addOption(OptionType.NUMBER, "timeout", "The rollcall will conclude automatically after this many minutes. Default: " + config.getProperty("default-rollcall-timeout"), false)
                ).queue();
    }
}