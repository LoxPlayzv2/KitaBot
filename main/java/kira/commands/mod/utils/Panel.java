package kira.commands.mod.utils;

import kira.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class Panel implements ICommand {
    @Override
    public String getName() {
        return "panel";
    }

    @Override
    public String getDescription() {
        return "Send a panel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "type", "Panel types: ticket, application, reactionrole", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role required = event.getGuild().getRoleById(1254874380304121956L);

        if (event.getMember() == null || !event.getMember().getRoles().contains(required)) {
            event.reply("You do not have permissions to use this command.").queue();
            return;
        }
        String panelType = event.getOption("type").getAsString().toLowerCase();

        switch (panelType) {
            case "ticket":
                sendTicketPanel(event);
                break;
            case "application":
                sendApplicationPanel(event);
                break;
            case "reactionrole":
                sendReactionRolePanel(event);
                break;
            case "verification":
                sendVerificationPanel(event);
                break;
            default:
                event.reply("Invalid panel type! Please choose from: ticket, application, reactionrole, verification.").queue();
                break;
        }
    }

    private void sendTicketPanel(SlashCommandInteractionEvent event) {
        EmbedBuilder ticketEmbed = new EmbedBuilder()
                .setTitle("Ticket Panel")
                .setDescription("Select a category below to create a private ticket.")
                .setColor(0x00FF00);

        StringSelectMenu ticketMenu = StringSelectMenu.create("ticketMenu")
                .setPlaceholder("Select a category...")
                .addOption("General", "general", "General question / inquiry")
                .addOption("Post a raid", "raided", "Post a raid for a faction")
                .build();

        event.replyEmbeds(ticketEmbed.build()).addActionRow(ticketMenu).queue();
    }

    private void sendApplicationPanel(SlashCommandInteractionEvent event) {
        EmbedBuilder applicationEmbed = new EmbedBuilder()
                .setTitle("Application Panel")
                .setDescription("Click the button below to start an application.")
                .setColor(0x0000FF);

        Button apply = Button.success("apply", "Apply");

        ActionRow row = ActionRow.of(apply);

        event.replyEmbeds(applicationEmbed.build()).addComponents(row).queue();
    }

    private void sendReactionRolePanel(SlashCommandInteractionEvent event) {
        EmbedBuilder reactionRoleEmbed = new EmbedBuilder()
                .setTitle("Reaction Role Panel")
                .setDescription("React below to get roles.\n" +
                        "\n- **Raids Ping:** <@&1255159366881902602> <:minecraftTNT:1255598708460880003>" +
                        "\n- **Giveaway Ping:** <@&1255597186838958132> <:5091giveaway:1255598706854461452>" +
                        "\n- **Clown Ping:** <@&1255597228148658217> <:2785honkhonk:1255598705642569910>" +
                        "\n- **Polls Ping:** <@&1255597352652247213> <:71019poll:1255598704279425217>")
                .setColor(0xFF0000)
                .setTimestamp(Instant.now())
                .setFooter("Kira | Public Faction");

        event.replyEmbeds(reactionRoleEmbed.build()).queue(response -> {
            response.retrieveOriginal().queue(message -> {
                message.addReaction(Emoji.fromFormatted("<:minecraftTNT:1255598708460880003>")).queue();
                message.addReaction(Emoji.fromFormatted("<:5091giveaway:1255598706854461452>")).queue();
                message.addReaction(Emoji.fromFormatted("<:2785honkhonk:1255598705642569910>")).queue();
                message.addReaction(Emoji.fromFormatted("<:71019poll:1255598704279425217>")).queue();
            });
        });
    }

    private void sendVerificationPanel(SlashCommandInteractionEvent event) {
        EmbedBuilder verificationEmbed = new EmbedBuilder()
                .setTitle("Verification")
                .setDescription("Click the button below to verify yourself.")
                .setColor(Color.green);

        Button verify = Button.success("verify", "Verify");

        ActionRow row = ActionRow.of(verify);

        event.replyEmbeds(verificationEmbed.build()).addComponents(row).queue();
    }
}
