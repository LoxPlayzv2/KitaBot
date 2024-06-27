package kira.commands.mod.level;

import kira.ICommand;
import kira.systems.LevelSystem;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class LevelResetAllCommand implements ICommand {
    private final LevelSystem levelSystem;

    public LevelResetAllCommand(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
    }

    @Override
    public String getName() {
        return "levelresetall";
    }

    @Override
    public String getDescription() {
        return "Reset the levels and experience for all users (Admin only)";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member != null && (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR))) {
            levelSystem.resetAllLevels();
            event.reply("Levels and experience reset for all users.").queue();
        } else {
            event.reply("You don't have permission to use this command.").queue();
        }
    }
}
