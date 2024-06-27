package kira.commands.mod.level;

import kira.ICommand;
import kira.systems.LevelSystem;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

public class LevelResetCommand implements ICommand {
    private final LevelSystem levelSystem;

    public LevelResetCommand(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
    }

    @Override
    public String getName() {
        return "levelreset";
    }

    @Override
    public String getDescription() {
        return "Reset the level and experience of a user (Admin only)";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.singletonList(
                new OptionData(OptionType.USER, "user", "The user to reset levels for", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member != null && (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR))) {
            Member targetMember = event.getOption("user").getAsMember();
            long userId = targetMember.getIdLong();
            levelSystem.resetUserData(userId);
            event.reply("Level and experience reset for " + targetMember.getEffectiveName()).queue();
        } else {
            event.reply("You don't have permission to use this command.").queue();
        }
    }
}
