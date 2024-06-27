package kira.commands.user.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import kira.listeners.music.GuildMusicManager;
import kira.ICommand;
import kira.listeners.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class PlayCommand implements ICommand {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Play a song from YouTube";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "query", "The song to play", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String query = event.getOption("query").getAsString();
        Guild guild = event.getGuild();
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

        GuildVoiceState voiceState = event.getMember().getVoiceState();

        if (voiceState == null || voiceState.getChannel() == null) {
            event.reply("You need to be in a voice channel to use this command.").queue();
            return;
        }
        AudioManager audioManager = guild.getAudioManager();
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceState.getChannel());
        }

        PlayerManager.getInstance().loadAndPlay(musicManager, query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.trackScheduler.queue(track);
                event.reply("Added to queue: " + track.getInfo().title).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    musicManager.trackScheduler.queue(track);
                }
                event.reply("Added playlist to queue: " + playlist.getName()).queue();
            }

            @Override
            public void noMatches() {
                event.reply("No matches found for: " + query).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.reply("Failed to load: " + exception.getMessage()).queue();
            }
        });
    }
}