package kira.commands.mod.moderation;

import kira.ICommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mute implements ICommand {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Mute a user";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to mute", true),
                new OptionData(OptionType.STRING, "reason", "The reason for the mute", false),
                new OptionData(OptionType.STRING, "duration", "How long to mute the user for (e.g., 1m, 5s, 3h)", false)
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
        OptionMapping durationOption = event.getOption("duration");
        String durationInput = durationOption == null ? null : durationOption.getAsString();

        event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(1255161513610576034L)).queue(success -> {
            if (durationInput != null) {
                long durationMillis = parseDuration(durationInput);
                Instant endTime = Instant.now().plusMillis(durationMillis);
                String formattedDuration = String.format("<t:%d:R>", endTime.getEpochSecond());

                EmbedBuilder durationEmbed = new EmbedBuilder()
                        .setTitle("Muted User")
                        .setDescription("**Muted user:** " + member.getAsMention() + "\n**Reason:** " + reason + "**Unmuted in:** " + formattedDuration);

                event.replyEmbeds(durationEmbed.build()).queue();

                scheduler.schedule(() -> {
                    event.getGuild().removeRoleFromMember(member, event.getGuild().getRoleById(1255161513610576034L)).queue();
                }, durationMillis, TimeUnit.MILLISECONDS);
            } else {
                EmbedBuilder durationEmbed = new EmbedBuilder()
                        .setTitle("Muted User")
                        .setDescription("**Muted user:** " + member.getAsMention() + "\n**Reason:** " + reason + "**Unmuted in:** " + "Never");

                event.replyEmbeds(durationEmbed.build()).queue();
            }
        });
    }

    private long parseDuration(String duration) {
        Pattern pattern = Pattern.compile("(\\d+)([smhd])");
        Matcher matcher = pattern.matcher(duration);
        long time = 0;

        if (matcher.matches()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "s":
                    time = TimeUnit.SECONDS.toMillis(value);
                    break;
                case "m":
                    time = TimeUnit.MINUTES.toMillis(value);
                    break;
                case "h":
                    time = TimeUnit.HOURS.toMillis(value);
                    break;
                case "d":
                    time = TimeUnit.DAYS.toMillis(value);
                    break;
            }
        }
        return time;
    }
}