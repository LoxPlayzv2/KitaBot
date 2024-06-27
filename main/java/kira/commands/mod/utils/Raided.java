package kira.commands.mod.utils;

import kira.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Raided implements ICommand {
    @Override
    public String getName() {
        return "raided";
    }

    @Override
    public String getDescription() {
        return "Announce when a faction got raided";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "faction_name", "The faction that raided", true),
                new OptionData(OptionType.STRING, "faction_raided", "The faction that got raided", true),
                new OptionData(OptionType.STRING, "duration", "How long did the raid take (e.g., 1m, 5s, 3h)", true),
                new OptionData(OptionType.STRING, "value", "The amount of value the faction had", false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role required = event.getGuild().getRoleById(1254874380304121956L);

        if (event.getMember() == null || !event.getMember().getRoles().contains(required)) {
            event.reply("You do not have permissions to use this command.").queue();
            return;
        }
        String factionName = event.getOption("faction_name").getAsString();
        String factionRaided = event.getOption("faction_raided").getAsString();
        String value = event.getOption("value") == null ? "0" : event.getOption("value").getAsString();
        String durationInput = event.getOption("duration").getAsString();

        long durationMillis = parseDuration(durationInput);
        String durationFormatted = formatDuration(durationMillis);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("<a:tnt:1254878149133729802> Faction Raids | Announcement")
                .setDescription("**" + factionRaided + "** has been raided by **" + factionName + "**\n**Raid lasted:** `" + durationFormatted + "`\n**Value lost:** `" + value + "`");
        event.reply("<@&1255159366881902602>").addEmbeds(embedBuilder.build()).queue(interactionHook ->
                interactionHook.retrieveOriginal().queue(message ->
                        message.addReaction(Emoji.fromFormatted("<a:tnt:1254878149133729802>")).queue()));
    }

    private long parseDuration(String input) {
        long duration = 0;
        StringBuilder numberPart = new StringBuilder();
        String unitPart = "";

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                numberPart.append(c);
            } else {
                unitPart = input.substring(i);
                break;
            }
        }

        long number = Long.parseLong(numberPart.toString());
        TimeUnit unit = TimeUnit.SECONDS; // Default to seconds

        switch (unitPart.toLowerCase()) {
            case "s":
                unit = TimeUnit.SECONDS;
                break;
            case "m":
                unit = TimeUnit.MINUTES;
                break;
            case "h":
                unit = TimeUnit.HOURS;
                break;
            case "d":
                unit = TimeUnit.DAYS;
                break;
        }

        duration = unit.toMillis(number);
        return duration;
    }

    private String formatDuration(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" day").append(days > 1 ? "s" : "").append(" ");
        }
        if (hours > 0) {
            sb.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(" ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(" ");
        }
        if (seconds > 0) {
            sb.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
        }

        return sb.toString().trim();
    }
}
