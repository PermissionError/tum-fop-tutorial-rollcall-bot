package dev.rayma.commands;

import dev.rayma.Launcher;
import dev.rayma.util.RollcallManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RollcallCommand extends ListenerAdapter {

    private static ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(4);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(!event.getInteraction().getName().equals("rollcall") || event.getGuild() == null || event.getMember() == null)
            return;

        Role tutorRole = event.getGuild().getRoleById(Launcher.config.getProperty("tutor-role-id"));

        if(tutorRole == null) {
            Launcher.LOGGER.error("Tutor Role not found.");
            System.exit(1);
        }

        if(!event.getMember().getRoles().contains(tutorRole)) {
            event
                    .reply("You are not authorised to initiate a rollcall.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        int timeout = event.getOption("timeout") == null ? Integer.parseInt(Launcher.config.getProperty("default-rollcall-timeout")) : event.getOption("timeout").getAsInt();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("New Rollcall Initiated");
        eb.setDescription("Please click the button below to indicate your attendance. This rollcall will conclude in " + timeout + " minute(s).");

        String id = UUID.randomUUID().toString();

        InteractionHook reply = event.reply(MessageCreateData.fromEmbeds(eb.build()))
                .addActionRow(Button.primary(id, "I'm here!"))
                .complete();


        RollcallManager.initiateRollcall(id);

        scheduler.schedule(() -> {
            List<String> students = RollcallManager.concludeRollcall(id);
            StringBuilder msg = new StringBuilder();
            msg.append("Students in attendance: \n");
            for(String student : students) {
                msg.append("\n").append(student);
            }
            event.getChannel().asTextChannel().sendMessage(msg).queue();
            reply.deleteOriginal().queue();
        }, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(!RollcallManager.rollcallExists(event.getButton().getId()))
            return;

        RollcallManager.markAttendance(event.getButton().getId(), event.getInteraction().getMember().getNickname());

        event.reply("Your attendance has been recorded.")
                .setEphemeral(true)
                .queue();
    }
}
