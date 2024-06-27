package kira.commands.mod.level;

import kira.ICommand;
import kira.systems.LevelSystem;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class LevelAddCommand implements ICommand {
    private final LevelSystem levelSystem;

    public LevelAddCommand(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
    }

    @Override
    public String getName() {
        return "leveladd";
    }

    @Override
    public String getDescription() {
        return "Add levels to a user (Staff only)";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to add levels to", true),
                new OptionData(OptionType.INTEGER, "levels", "The number of levels to add", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR)) {
            Member targetMember = event.getOption("user").getAsMember();
            int levelsToAdd = event.getOption("levels").getAsInt();

            levelSystem.addExperience(targetMember.getIdLong(), levelsToAdd);
            event.reply("Added " + levelsToAdd + " levels to " + targetMember.getEffectiveName()).queue();
        } else {
            event.reply("You don't have permission to use this command.").queue();
        }
    }
}