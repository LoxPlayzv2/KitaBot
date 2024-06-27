package kira.commands.mod.utils;

import kira.ICommand;
import kira.listeners.ReactionRolePanel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class ReactionRoleCommand implements ICommand {
    private final ReactionRolePanel reactionRolePanel;

    public ReactionRoleCommand(ReactionRolePanel reactionRolePanel) {
        this.reactionRolePanel = reactionRolePanel;
    }

    @Override
    public String getName() {
        return "reactionrole";
    }

    @Override
    public String getDescription() {
        return "Send the reaction role panel";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role required = event.getGuild().getRoleById(1254874380304121956L);

        if (event.getMember() == null || !event.getMember().getRoles().contains(required)) {
            event.reply("You do not have permissions to use this command.").queue();
            return;
        }

        reactionRolePanel.sendReactionRolePanel(event);
    }
}
