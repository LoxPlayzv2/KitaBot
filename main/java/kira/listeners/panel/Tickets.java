package kira.listeners.panel;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Tickets extends ListenerAdapter {
    private final Map<String, Long> cooldowns = new HashMap<>(); // Map to store user cooldowns

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("ticketMenu")) {
            String select = event.getValues().get(0);

            switch (select) {
                case "general":
                    handleGeneralTicket(event);
                    break;
                case "raided":
                    handleRaidedTicket(event);
                    break;
                default:
                    event.reply("Invalid ticket selection.").setEphemeral(true).queue();
                    break;
            }
        }
    }

    private void handleGeneralTicket(StringSelectInteractionEvent event) {
        String userId = event.getUser().getId();
        if (isOnCooldown(userId)) {
            event.reply("You are on cooldown. Please wait before creating another ticket.").setEphemeral(true).queue();
            event.getInteraction().editSelectMenu(event.getSelectMenu().createCopy()
                    .setPlaceholder("Select a category...").build()).queue();
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.reply("Unable to retrieve member information.").setEphemeral(true).queue();
            return;
        }

        String userName = member.getUser().getName();
        Category category = event.getGuild().getCategoryById(1255221470238343218L);

        category.createTextChannel("general-" + userName)
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getRoleById(1254874380304121956L), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .queue(generalTicketChannel -> {
                    event.reply("Your ticket has been created! View it here: " + generalTicketChannel.getAsMention()).setEphemeral(true).queue();

                    EmbedBuilder generalEmbed = new EmbedBuilder()
                            .setTitle("General Ticket")
                            .setDescription("Welcome to the **General** ticket.")
                            .setColor(Color.GREEN)
                            .setTimestamp(Instant.now());

                    Button close = Button.danger("close", "Close Ticket");

                    ActionRow row = ActionRow.of(close);

                    event.getInteraction().editSelectMenu(event.getSelectMenu().createCopy()
                            .setPlaceholder("Select a category...").build()).queue();

                    generalTicketChannel.sendMessage(member.getAsMention()).addEmbeds(generalEmbed.build()).addComponents(row).queue();

                    applyCooldown(userId);
                });
    }

    private void handleRaidedTicket(StringSelectInteractionEvent event) {
        String userId = event.getUser().getId();
        if (isOnCooldown(userId)) {
            event.reply("You are on cooldown. Please wait before creating another ticket.").setEphemeral(true).queue();
            event.getInteraction().editSelectMenu(event.getSelectMenu().createCopy()
                    .setPlaceholder("Select a category...").build()).queue();
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.reply("Unable to retrieve member information.").setEphemeral(true).queue();
            return;
        }

        String userName = member.getUser().getName();
        Category category = event.getGuild().getCategoryById(1255221470238343218L);

        category.createTextChannel("post-raid-" + userName)
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getRoleById(1254874380304121956L), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .queue(postRaidChannel -> {
                    event.reply("Your ticket has been created! View it here: " + postRaidChannel.getAsMention()).setEphemeral(true).queue();

                    EmbedBuilder raidedEmbed = new EmbedBuilder()
                            .setTitle("Post a Raid Ticket")
                            .setDescription("Welcome to the **Post a Raid** ticket.")
                            .addField("Provide the following:", "**Your Faction name:**\n**Faction raided name:**\n**Raid duration:**\n**Value gotten:**\n**Screenshot:** (of orbit map *not required*)", false)
                            .setColor(Color.RED)
                            .setTimestamp(Instant.now());

                    Button close = Button.danger("close", "Close Ticket");

                    ActionRow row = ActionRow.of(close);

                    event.getInteraction().editSelectMenu(event.getSelectMenu().createCopy()
                            .setPlaceholder("Select a category...").build()).queue();

                    postRaidChannel.sendMessage(member.getAsMention()).addEmbeds(raidedEmbed.build()).addComponents(row).queue();

                    applyCooldown(userId);
                });
    }

    private boolean isOnCooldown(String userId) {
        long cooldownSeconds = 60; // 60 seconds cooldown
        long currentTime = Instant.now().getEpochSecond();
        if (cooldowns.containsKey(userId)) {
            long lastInteraction = cooldowns.get(userId);
            return currentTime < lastInteraction + cooldownSeconds;
        }
        return false; // Not on cooldown if no previous interaction
    }

    private void applyCooldown(String userId) {
        cooldowns.put(userId, Instant.now().getEpochSecond());
    }
}
