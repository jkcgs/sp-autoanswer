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
        if(!event.getPlayer().hasPermission("autoanswer.listen")) return;

        AnswerConfig conf = plugin.getAnswerConfig();
        String msg = event.getMessage();
        String answer = conf.getAnswer(msg.toLowerCase());

        if(answer != null) {
            if(plugin.getConfig().getBoolean("broadcast-chat")) {
                String bcmsg = plugin.getConfig().getString("messages.broadcast-format");
                if(bcmsg == null) {
                    logger.warning("Missing 'messages.broadcast-format' from config!");
                    bcmsg = "AutoAnswer -> {player}: {answer}";
                }

                bcmsg = bcmsg.replaceAll("\\{player\\}", event.getPlayer().getName());
                bcmsg = bcmsg.replaceAll("\\{answer\\}", answer);
                plugin.getServer().broadcastMessage(bcmsg);
            } else {
                String userMsg = plugin.getConfig().getString("messages.answer-format");
                if(userMsg == null) {
                    plugin.getLogger().warning("Missing 'messages.answer-format' from config!");
                    userMsg = "AutoAnswer: {answer}";
                }

                userMsg = userMsg.replaceAll("\\{answer\\}", answer);
                event.getPlayer().sendMessage(userMsg);
            }

        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(!event.getPlayer().hasPermission("autoanswer.listen")) return;

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
        if(answer == null) return;


        String userMsg = plugin.getConfig().getString("messages.answer-format");
        if(userMsg == null) {
            logger.warning("Missing 'messages.answer-format' from config!");
            userMsg = "AutoAnswer: {answer}";
        }

        userMsg = userMsg.replaceAll("\\{answer\\}", answer);
        event.getPlayer().sendMessage(userMsg);

        if(cmdConf.tellStaff) {
            String bcmsg = plugin.getConfig().getString("messages.tell-staff-format");
            if(bcmsg == null) {
                logger.warning("Missing 'messages.tell-staff-format' from config!");
                bcmsg = "AutoAnswer -> {player}: {answer}";
            }

            bcmsg = bcmsg.replaceAll("\\{player\\}", event.getPlayer().getName());
            bcmsg = bcmsg.replaceAll("\\{answer\\}", answer);
            plugin.getServer().broadcast(bcmsg, "autoanswer.inform");
        }
    }
}