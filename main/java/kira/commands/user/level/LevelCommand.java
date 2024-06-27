package kira.commands.user.level;

import kira.ICommand;
import kira.systems.LevelSystem;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class LevelCommand implements ICommand {
    private final LevelSystem levelSystem;

    public LevelCommand(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
    }

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public String getDescription() {
        return "Check your current level and experience";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList(); // No options for this command
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        long userId = user.getIdLong();

        int level = levelSystem.getLevel(userId);
        int experience = levelSystem.getExperience(userId);
        int experienceRequired = levelSystem.getExperienceRequired(level + 1);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Level Information")
                .setColor(Color.BLUE)
                .addField("User", user.getAsTag(), true)
                .addField("Level", String.valueOf(level), true)
                .addField("Experience", experience + "/" + experienceRequired, true);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
