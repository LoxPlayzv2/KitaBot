package kira.listeners.panel;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class Verification extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        Role unverified = event.getGuild().getRoleById(1255202613377499257L);
        Role verified = event.getGuild().getRoleById(1254520346795839579L);
        if (event.getButton().getId().equals("verify")) {
            event.reply("You have been `verified`!").setEphemeral(true).queue();
            event.getGuild().removeRoleFromMember(member, unverified).queue();
            event.getGuild().addRoleToMember(member, verified).queueAfter(1, TimeUnit.SECONDS);
        }
    }
}