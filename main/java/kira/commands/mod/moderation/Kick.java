package kira.commands.mod.moderation;

import kira.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;

public class Kick implements ICommand {
    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Kick a user";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "User to kick", true),
                new OptionData(OptionType.STRING, "reason", "Reason for kicking", false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role required = event.getGuild().getRoleById(1254874380304121956L);

        if (event.getMember() == null || !event.getMember().getRoles().contains(required)) {
            event.reply("You do not have permissions to use this command.").queue();
            return;
        }
        Member member = event.getOption("user").getAsMember();
        String reason = event.getOption("reason") == null ? "No reason provided" : event.getOption("reason").getAsString();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("User kicked")
                .setDescription("**User:** " + member.getAsMention() + "\n**Reason:** " + reason)
                .setThumbnail(member.getUser().getAvatarUrl())
                .setTimestamp(Instant.now());
        event.getGuild().kick(member, reason).queue();
        event.reply("Kicked " + member.getEffectiveName() + " for " + reason).queue();
    }
}
