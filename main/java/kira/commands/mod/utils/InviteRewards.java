package kira.commands.mod.utils;

import kira.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;

public class InviteRewards implements ICommand {
    @Override
    public String getName() {
        return "inviterewards";
    }

    @Override
    public String getDescription() {
        return "Send the invite rewards embed";
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
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Invite Rewards")
                .setDescription("- **1** Invite = 2x Immortal Kits\n- **3** Invites = 2x Immortal Kits + 1x Eternal Kit\n- **5** Invites = 6x Eternal Kits or 16x Immortal Kits" +
                        "\n\n- **15** Invites = 2x Treasure or 2x Special crates\n\n**NOTE**__ **Invites will be reset after claiming! Create a ticket to claim <#1254875707666858105>**")
                .setImage("https://cdn.discordapp.com/attachments/1254851224096735387/1255591033249464394/d4ljh34-57d0f5ae-2aa2-44ee-abc1-364b8d3b9087.png?ex=667dafc6&is=667c5e46&hm=ac45972153e19f2b9dea3314b8b86bb94c21cf652e64dbd0f4f4a40fc9a46062&")
                .setTimestamp(Instant.now());
        event.replyEmbeds(embed.build()).queue();
    }
}
