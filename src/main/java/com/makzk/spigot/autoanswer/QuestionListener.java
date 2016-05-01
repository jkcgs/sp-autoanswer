package com.makzk.spigot.autoanswer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.logging.Logger;

public class QuestionListener implements Listener {
    private AutoAnswer plugin;
    private Logger logger;
    public QuestionListener(AutoAnswer plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        AnswerConfig conf = plugin.getAnswerConfig();
        String msg = event.getMessage();
        String answer = conf.getAnswer(msg.toLowerCase());

        if(answer != null) {
            plugin.getServer().broadcastMessage("[AutoAnswer] " + event.getPlayer().getName() + " -> " + answer);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        AnswerConfig conf = plugin.getAnswerConfig();
        String args[] = event.getMessage().split(" ");
        String command = args[0].substring(1);
        String message = "";

        CommandConfig cmdConf = conf.getCommandConfig(command);
        if(cmdConf == null) {
            return;
        }

        // Process the command only if it's long enough :$
        if(args.length > cmdConf.offset+1) {
            // Cut out the message
            String msgspl[] = Arrays.copyOfRange(args, cmdConf.offset+1, args.length);
            message = String.join(" ", (CharSequence[]) msgspl);
        }

        String answer = conf.getAnswer(message.toLowerCase());
        if(answer != null) {
            event.getPlayer().sendMessage("[AutoAnswer] -> " + answer);
            plugin.getServer().broadcast("{AutoAnswer} " + event.getPlayer().getName() + " -> " + answer, "autoanswer.listen");
        }
    }
}