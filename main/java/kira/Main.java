package kira;

import io.github.cdimascio.dotenv.Dotenv;
import kira.commands.mod.level.LevelResetAllCommand;
import kira.commands.mod.level.LevelResetCommand;
import kira.commands.mod.moderation.*;
import kira.commands.mod.utils.InviteRewards;
import kira.commands.mod.utils.Panel;
import kira.commands.mod.utils.Raided;
import kira.commands.mod.utils.ReactionRoleCommand;
import kira.commands.user.level.LevelCommand;
import kira.commands.user.music.PlayCommand;
import kira.giveaway.Gcreate;
import kira.listeners.ReactionRolePanel;
import kira.listeners.logger.Logs;
import kira.listeners.panel.Tickets;
import kira.listeners.panel.Verification;
import kira.listeners.panel.ticketbuttons.TicketButtons;
import kira.systems.LevelSystem;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TOKEN");
        JDA jda = JDABuilder.createLight(token)
                .setActivity(Activity.playing("pikanetwork.net | OPFactions"))
                .enableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.VOICE)
                .setEnabledIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .build();

        LevelSystem levelSystem = new LevelSystem();

        CommandManager manager = new CommandManager();
        manager.add(new PlayCommand());
        manager.add(new Raided());
        manager.add(new Purge());
        manager.add(new Mute());
        manager.add(new Setupmute());
        manager.add(new Roleall());
        manager.add(new Panel());
        manager.add(new Ban());
        manager.add(new Gcreate());
        manager.add(new InviteRewards());

        jda.addEventListener(manager);
        jda.addEventListener(new Logs());
        jda.addEventListener(new Tickets());
        jda.addEventListener(new Verification());
        jda.addEventListener(new TicketButtons());
        jda.addEventListener(new LevelSystem());
        jda.addEventListener(new ReactionRolePanel());
        manager.add(new ReactionRoleCommand(new ReactionRolePanel()));
        manager.add(new LevelCommand(levelSystem));
        manager.add(new LevelResetCommand(levelSystem));
        manager.add(new LevelResetAllCommand(levelSystem));
    }
}