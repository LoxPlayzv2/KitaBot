package kira.giveaway;

import kira.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class Gcreate implements ICommand {

    private static final Map<Integer, ScheduledFuture<?>> giveawayTasks = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    @Override
    public String getName() {
        return "gcreate";
    }

    @Override
    public String getDescription() {
        return "Creates a giveaway in the channel";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.CHANNEL, "channel", "The channel you want to create the giveaway in", true)
                        .setChannelTypes(ChannelType.TEXT),
                new OptionData(OptionType.STRING, "time", "How long the giveaway should be, for example: 1m (minutes), 1h (hours), 1d (days), 1y (years)", true),
                new OptionData(OptionType.STRING, "winners", "How many users can win the giveaway?", true),
                new OptionData(OptionType.STRING, "prize", "The prize to win", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Color createdColor = new Color(0x2ECC71);

        TextChannel channel = event.getOption("channel").getAsChannel().asTextChannel();
        String prize = event.getOption("prize").getAsString();
        if (prize == null || prize.isBlank()) {
            event.reply("Please specify the prize for the giveaway.").setEphemeral(true).queue();
            return;
        }

        String durationStr = event.getOption("time").getAsString();
        if (durationStr == null || durationStr.isBlank()) {
            event.reply("Please specify the duration for the giveaway.").setEphemeral(true).queue();
            return;
        }

        long durationSeconds = parseDuration(durationStr);
        if (durationSeconds <= 0) {
            event.reply("Invalid duration format. Please use format like '5s', '5m', '1h', '1d'.").setEphemeral(true).queue();
            return;
        }

        String winnersStr = event.getOption("winners").getAsString();
        if (winnersStr == null || winnersStr.isBlank()) {
            event.reply("Please specify the number of winners for the giveaway.").setEphemeral(true).queue();
            return;
        }
        int winnersCount = Integer.parseInt(winnersStr);
        int giveawayId = generateNineDigitId();
        Instant endTime = Instant.now().plusSeconds(durationSeconds);

        EmbedBuilder giveawayEmbed = new EmbedBuilder();

        giveawayEmbed.setTitle("\uD83C\uDF89 Giveaway: " + prize);
        giveawayEmbed.setDescription("React with :tada: to enter the giveaway!");
        giveawayEmbed.addField("\u23F0 Ending", String.format("<t:%d:R>", endTime.getEpochSecond()), true);
        giveawayEmbed.addField("\uD83C\uDFC6 Winners", winnersStr, true)
                .setFooter("Giveaway ID: " + giveawayId, event.getGuild().getIconUrl());
        giveawayEmbed.setColor(createdColor);
        giveawayEmbed.setTimestamp(Instant.now());

        channel.sendMessage("<@&1255597186838958132>").addEmbeds(giveawayEmbed.build()).queue(giveawayMessage -> {
            giveawayMessage.addReaction(Emoji.fromUnicode("\uD83C\uDF89")).queue();
            event.reply("The giveaway has been created!").setEphemeral(true).queue();

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
                System.out.println("Giveaway timer ended for ID: " + giveawayId);
                if (giveawayTasks.containsKey(giveawayId)) {
                    List<User> participants = giveawayMessage.retrieveReactionUsers(Emoji.fromUnicode("\uD83C\uDF89")).complete();
                    participants.removeIf(User::isBot);
                    Collections.shuffle(participants);

                    if (participants.isEmpty()) {
                        updateGiveawayMessageForNoEntries(giveawayMessage, endTime, winnersCount, prize);
                    } else {
                        List<User> winners = participants.subList(0, Math.min(participants.size(), winnersCount));
                        announceWinners(giveawayMessage, winners, prize, endTime, event);
                    }

                    giveawayTasks.remove(giveawayId);
                }
            }, durationSeconds, TimeUnit.SECONDS);

            giveawayTasks.put(giveawayId, scheduledFuture);
        });
    }

    private int generateNineDigitId() {
        return random.nextInt(1_000_000_000);
    }

    private void updateGiveawayMessageForNoEntries(Message giveawayMessage, Instant endTime, int winnersCount, String prizeText) {
        Color endedColor = new Color(0xE74C3C);
        EmbedBuilder noEntriesEmbed = new EmbedBuilder()
                .setColor(endedColor)
                .setTitle(":x: **Giveaway Ended**")
                .setDescription("Unfortunately, there were no entries for this giveaway.")
                .addField("\uD83C\uDF81 **Prize**", prizeText, true)
                .addField("\uD83D\uDD51 **Ended**", String.format("<t:%d:R>", endTime.getEpochSecond()), true)
                .addField("\uD83C\uDFC6 **Winners**", "No winners", true)
                .setFooter("Thank you for your interest!", giveawayMessage.getGuild().getIconUrl())
                .setTimestamp(Instant.now());

        giveawayMessage.editMessageEmbeds(noEntriesEmbed.build()).queue();
    }

    private void announceWinners(Message giveawayMessage, List<User> winners, String prizeText, Instant endTime, SlashCommandInteractionEvent event) {
        Color endedColor = new Color(0xE74C3C); // Subtle red color
        StringBuilder winnersList = new StringBuilder();
        for (int i = 0; i < winners.size(); i++) {
            winnersList.append(winners.get(i).getAsMention());
            if (i < winners.size() - 1) {
                winnersList.append(", ");
            }
        }

        String message = winners.size() == 1
                ? "Congratulations to " + winnersList + " on winning: `" + prizeText + "`"
                : "Congratulations to " + winnersList + " on winning: `" + prizeText + "`";
        giveawayMessage.reply(message).queue();

        EmbedBuilder updatedEmbed = new EmbedBuilder()
                .setColor(endedColor)
                .setTitle(":tada: **Giveaway Ended**")
                .setDescription("Thank you for participating in the giveaway!")
                .addField("\uD83C\uDF81 **Prize**", prizeText, true)
                .addField("\uD83D\uDD51 **Ended**", String.format("<t:%d:R>", endTime.getEpochSecond()), true)
                .addField("\uD83C\uDFC6 **Winners**", winnersList.toString(), true)
                .setFooter("Hosted by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());

        giveawayMessage.editMessageEmbeds(updatedEmbed.build()).queue();
    }

    private long parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isBlank()) {
            return -1;
        }

        char unit = durationStr.charAt(durationStr.length() - 1);
        long durationValue = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
        switch (unit) {
            case 's':
                return durationValue;
            case 'm':
                return durationValue * 60;
            case 'h':
                return durationValue * 3600;
            case 'd':
                return durationValue * 86400;
            default:
                return -1;
        }
    }

    public static Map<Integer, ScheduledFuture<?>> getGiveawayTasks() {
        return giveawayTasks;
    }
}