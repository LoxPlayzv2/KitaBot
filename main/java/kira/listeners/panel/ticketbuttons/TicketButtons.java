package kira.listeners.panel.ticketbuttons;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TicketButtons extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().equals("close")) {
            Role supportRole = event.getGuild().getRoleById(1254874380304121956L);
            TextChannel channel = (TextChannel) event.getChannel(); // Get the text channel where the button was clicked
            Instant endTime = Instant.now().plusSeconds(5);

            if (!event.getMember().getRoles().contains(supportRole)) {
                event.reply("You do not have permission to use this button!").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Ticket Solved")
                    .setDescription("This ticket is getting deleted " + String.format("<t:%d:R>", endTime.getEpochSecond()) + "\n**Deleted by:** " + event.getUser().getAsMention())
                    .setColor(Color.RED);

            event.replyEmbeds(embed.build()).queue();
            channel.delete().queueAfter(5, TimeUnit.SECONDS);
        }
    }
}