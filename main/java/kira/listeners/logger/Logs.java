package kira.listeners.logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Logs extends ListenerAdapter {
    private static final Map<String, String> messageCache = new ConcurrentHashMap<>();
    private static final long LOG_CHANNEL_ID = 1254851224096735387L;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()) {
            return;
        }

        String messageId = event.getMessageId();
        String messageContent = event.getMessage().getContentRaw();
        String authorId = event.getAuthor().getId();
        messageCache.put(messageId, messageContent + ":" + authorId);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        TextChannel logChannel = event.getGuild().getTextChannelById(LOG_CHANNEL_ID);
        if (logChannel != null) {
            String messageId = event.getMessageId();
            String[] messageData = messageCache.getOrDefault(messageId, "Unknown message content:").split(":", 2);
            String messageContent = messageData[0];
            String authorId = messageData.length > 1 ? messageData[1] : "";
            TextChannel channel = event.getChannel().asTextChannel();
            String channelMention = channel != null ? channel.getAsMention() : "Unknown Channel";

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Message Deleted")
                    .setDescription(messageContent)
                    .addField("Channel", channelMention, false);

            if (!authorId.isEmpty() && authorId.matches("\\d+")) { // Ensure authorId is numeric
                User user = event.getJDA().getUserById(authorId);
                if (user != null) {
                    embed.addField("Deleted by", user.getAsMention(), false);
                }
            }

            embed.setColor(Color.RED)
                    .setTimestamp(Instant.now());

            logChannel.sendMessageEmbeds(embed.build())
                    .queue(null, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e -> {
                        System.out.println("Failed to send message delete log: " + e.getMessage());
                    }));
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member bot = event.getGuild().getSelfMember();
        Member member = event.getMember();
        Role role = event.getGuild().getRoleById(1255202613377499257L);
        Role botRole = event.getGuild().getRoleById(1254873844401836093L);
        event.getGuild().getTextChannelById(1254519441044148417L)
                .sendMessage(event.getUser().getAsMention() + "** has joined the server!**").queue();
        if (event.getMember().getUser().isBot()) {
            event.getGuild().addRoleToMember(bot, botRole).queue();
        }
        event.getGuild().addRoleToMember(member, role).queue();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        TextChannel logChannel = event.getGuild().getTextChannelById(LOG_CHANNEL_ID);
        if (logChannel != null) {
            logChannel.sendMessage("Member left: " + event.getUser().getAsTag()).queue();
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        TextChannel logChannel = event.getGuild().getTextChannelById(LOG_CHANNEL_ID);
        if (logChannel != null) {
            logChannel.sendMessage("Member banned: " + event.getUser().getAsTag()).queue();
        }
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        TextChannel logChannel = event.getGuild().getTextChannelById(LOG_CHANNEL_ID);
        if (logChannel != null) {
            logChannel.sendMessage("Member unbanned: " + event.getUser().getAsTag()).queue();
        }
    }
}
