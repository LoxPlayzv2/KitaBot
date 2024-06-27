package kira.commands.mod.moderation;

import kira.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Roleall implements ICommand {
    @Override
    public String getName() {
        return "roleall";
    }

    @Override
    public String getDescription() {
        return "Give a role to all users in the server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.ROLE, "role", "The role to give", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role required = event.getGuild().getRoleById(1254874380304121956L);
        Role role = event.getOption("role").getAsRole();
        if (event.getMember() == null || !event.getMember().getRoles().contains(required)) {
            event.reply("You do not have permissions to use this command.").queue();
            return;
        }

        event.deferReply().queue();

        event.getGuild().loadMembers().onSuccess(members -> {
            List<CompletableFuture<Void>> futures = members.stream()
                    .map(member -> event.getGuild().addRoleToMember(member, role).submit()
                            .thenRun(() -> {
                            }).exceptionally(error -> {
                                event.getHook().sendMessageEmbeds(
                                        new EmbedBuilder()
                                                .setDescription("An error occurred while giving the role to " + member.getEffectiveName())
                                                .build()
                                ).queue();
                                return null;
                            }))
                    .collect(Collectors.toList());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.thenRun(() -> {
                long count = members.stream().filter(member -> member.getRoles().contains(role)).count();
                event.getHook().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setDescription("Role `" + role.getName() + "` has been given to " + count + " members in the server.")
                                .build()
                ).queue();
            });
        }).onError(error -> {
            event.getHook().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setDescription("An error occurred while loading the members.")
                            .build()
            ).queue();
        });
    }
}
