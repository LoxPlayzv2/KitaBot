package kira.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ReactionRolePanel extends ListenerAdapter {
    private static final long REACTION_ROLE_MESSAGE_ID = 1255608185054691483L;
    private static final Map<String, Long> emojiRoleMap = new HashMap<>();

    static {
        emojiRoleMap.put("minecraftTNT", 1255159366881902602L);
        emojiRoleMap.put("5091giveaway", 1255597186838958132L);
        emojiRoleMap.put("2785honkhonk", 1255597228148658217L);
        emojiRoleMap.put("71019poll", 1255597352652247213L);
    }

    public void sendReactionRolePanel(SlashCommandInteractionEvent event) {
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
                message.addReaction(Emoji.fromCustom("minecraftTNT", 1255598708460880003L, false)).queue();
                message.addReaction(Emoji.fromCustom("5091giveaway", 1255598706854461452L, false)).queue();
                message.addReaction(Emoji.fromCustom("2785honkhonk", 1255598705642569910L, false)).queue();
                message.addReaction(Emoji.fromCustom("71019poll", 1255598704279425217L, false)).queue();
            });
        });
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        TextChannel channel = event.getGuild().getTextChannelById(1254851224096735387L);
        if (event.getUser().isBot() || event.getMessageIdLong() != REACTION_ROLE_MESSAGE_ID) return;

        Emoji emoji = event.getReaction().getEmoji();
        String emojiName = emoji.getName();
        Long roleId = emojiRoleMap.get(emojiName);

        if (roleId != null) {
            Role role = event.getGuild().getRoleById(roleId);
            if (role != null) {
                event.getGuild().addRoleToMember(event.getMember(), role).queue(
                        success -> channel.sendMessage("Assigned role: " + role.getName()),
                        failure -> channel.sendMessage("Failed to assign role: " + role.getName())
                );
            } else {
                channel.sendMessage("Role not found for ID: " + roleId);
            }
        } else {
            channel.sendMessage("Unknown emoji: " + emojiName);
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        TextChannel channel = event.getGuild().getTextChannelById(1254851224096735387L);
        if (event.getMessageIdLong() != REACTION_ROLE_MESSAGE_ID) return;

        Emoji emoji = event.getReaction().getEmoji();
        String emojiName = emoji.getName();
        Long roleId = emojiRoleMap.get(emojiName);

        if (roleId != null) {
            Role role = event.getGuild().getRoleById(roleId);
            if (role != null) {
                event.getGuild().retrieveMemberById(event.getUserId()).queue(
                        member -> {
                            if (member != null) {
                                event.getGuild().removeRoleFromMember(member, role).queue(
                                        success -> channel.sendMessage("Removed role: " + role.getName()),
                                        failure -> channel.sendMessage("Failed to remove role: " + role.getName())
                                );
                            } else {
                                channel.sendMessage("Member not found for ID: " + event.getUserId());
                            }
                        },
                        failure -> channel.sendMessage("Failed to retrieve member: " + failure.getMessage())
                );
            } else {
                channel.sendMessage("Role not found for ID: " + roleId);
            }
        } else {
            channel.sendMessage("Unknown emoji: " + emojiName);
        }
    }
}
