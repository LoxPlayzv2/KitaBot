package kira.commands.mod.moderation;

import kira.ICommand;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Purge implements ICommand {
    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "Deletes a specified number of messages from the channel.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "amount", "The number of messages to delete", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role required = event.getGuild().getRoleById(1254874380304121956L);

        if (event.getMember() == null || !event.getMember().getRoles().contains(required)) {
            event.reply("You do not have permissions to use this command.").queue();
            return;
        }

        int numMessages = event.getOption("amount").getAsInt();

        if (numMessages < 1 || numMessages > 100) {
            event.reply("Please specify a number between 1 and 100.").setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        event.getChannel().getIterableHistory()
                .takeAsync(numMessages)
                .thenAccept(messages -> {
                    event.getChannel().purgeMessages(messages);
                    event.getHook().sendMessage("Successfully deleted " + numMessages + " messages.")
                            .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
                })
                .exceptionally(throwable -> {
                    event.getHook().sendMessage("Failed to delete messages.").setEphemeral(true).queue();
                    return null;
                });
    }
}
