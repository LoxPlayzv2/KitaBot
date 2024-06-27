package kira.commands.mod.moderation;

import kira.ICommand;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class Setupmute implements ICommand {
    @Override
    public String getName() {
        return "setupmute";
    }

    @Override
    public String getDescription() {
        return "Will setup the mute";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role required = event.getGuild().getRoleById(1254874380304121956L);

        if (event.getMember() == null || !event.getMember().getRoles().contains(required)) {
            event.reply("You do not have permissions to use this command.").queue();
            return;
        }
        Role muteRole = event.getGuild().getRolesByName("Muted", true).stream().findFirst().orElse(null);

        if (muteRole == null) {
            RoleAction roleAction = event.getGuild().createRole()
                    .setName("Muted")
                    .setPermissions(Permission.EMPTY_PERMISSIONS);

            muteRole = roleAction.complete();
        }

        // Deny send messages permission in all text channels for the Muted role
        for (GuildChannel channel : event.getGuild().getChannels()) {
            if (channel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channel;
                PermissionOverride override = textChannel.getPermissionOverride(muteRole);
                if (override == null) {
                    textChannel.upsertPermissionOverride(muteRole).setDenied(Permission.MESSAGE_SEND).queue();
                } else {
                    override.getManager().deny(Permission.MESSAGE_SEND).queue();
                }
            } else if (channel instanceof VoiceChannel) {
                VoiceChannel voiceChannel = (VoiceChannel) channel;
                PermissionOverride override = voiceChannel.getPermissionOverride(muteRole);
                if (override == null) {
                    voiceChannel.upsertPermissionOverride(muteRole).setDenied(Permission.VOICE_SPEAK).queue();
                } else {
                    override.getManager().deny(Permission.VOICE_SPEAK).queue();
                }
            }
        }

        event.reply("Mute role setup complete.").queue();
    }
}
