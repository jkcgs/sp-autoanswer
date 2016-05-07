package com.makzk.spigot.autoanswer;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AnswerConfig {
    private JavaPlugin plugin;
    private Logger logger;
    private Map<String, CommandConfig> commands = new HashMap<>();
    private Map<String, String> questions = new HashMap<>();
    private Map<String, String> answers = new HashMap<>();

    public AnswerConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    /**
     * Reloads the config. If it was not previously loaded, it's initialized.
     * @return A <code>boolean</code> depending if the config was loaded.
     */
    public void reload() {
        FileConfiguration config = plugin.getConfig();

        // Set up answers
        answers = new HashMap<>();
        if(config.contains("answers")) {
            Map<String, Object> answersConfig = config.getConfigurationSection("answers").getValues(true);
            for (Map.Entry<String, Object> entry : answersConfig.entrySet()) {
                if(entry.getValue() instanceof String) {
                    answers.put(entry.getKey(), (String) entry.getValue());
                } else {
                    logger.info("Invalid value for answer ID " + entry.getKey());
                }
            }
        } else {
            logger.info("The config does not have answers! Not even for your existence!");
        }

        // Set up questions
        questions = new HashMap<>();
        if(config.contains("questions")) {
            Map<String, Object> questionsConfig = config.getConfigurationSection("questions").getValues(true);
            for (Map.Entry<String, Object> entry : questionsConfig.entrySet()) {
                String answerID = "";
                if(entry.getValue() instanceof Integer) {
                    answerID = entry.getValue().toString();
                } else if(entry.getValue() instanceof String) {
                    answerID = (String) entry.getValue();
                } else if(answerID.isEmpty()) {
                    logger.info("Invalid value for question regex " + entry.getKey());
                    continue;
                }

                if(questions.containsKey(entry.getKey()) && !answers.containsKey(answerID)) {
                    logger.warning("Answer ID " + entry.getValue() + " not found");
                } else {
                    questions.put(entry.getKey(), answerID);
                }
            }

            logger.info("Registered " + questions.size() + " questions");
        } else {
            logger.info("The config does not have questions!");
        }

        commands = new HashMap<>();
        if(config.contains("listen-commands")) {
            Map<String, Object> cmdsConfig = config.getConfigurationSection("listen-commands").getValues(false);
            for (Map.Entry<String, Object> entry : cmdsConfig.entrySet()) {
                String cmdName = entry.getKey().toLowerCase();
                if(commands.containsKey(cmdName)) continue;

                CommandConfig newCmd = new CommandConfig(cmdName);
                ConfigurationSection ms = config.getConfigurationSection("listen-commands").getConfigurationSection(cmdName);
                if(ms == null) continue;

                if(ms.contains("cancel")) newCmd.cancel = ms.getBoolean("cancel");
                if(ms.contains("tell-staff")) newCmd.tellStaff = ms.getBoolean("tell-staff");
                if(ms.contains("args-offset")) newCmd.offset = ms.getInt("args-offset");
                commands.put(cmdName, newCmd);
                logger.info("Listening to command '" + cmdName + "'");
            }
        }
    }

    /**
     * Gives an answer to a question.
     * @param question The question that needs to be answered.
     * @return A possible answer, <code>null</code> if no answer was found.
     */
    public String getAnswer(String question) {
        for (Map.Entry<String, String> entry : questions.entrySet()) {
            if(question.matches(entry.getKey())) {
                if(!answers.containsKey(entry.getValue())) {
                    logger.warning("Question matches, but we don't have answer ID " + entry.getValue());
                    break;
                }

                return answers.get(entry.getValue());
            }
        }
        return null;
    }

    /**
     * Retrieves the configuration for a command
     * @param command The command to get its config
     * @return null if the command is not registered
     */
    public CommandConfig getCommandConfig(String command) {
        command = command.toLowerCase();
        if(!commands.containsKey(command)) {
            return null;
        }

        return commands.get(command);
    }
}

class CommandConfig {
    public String commandName;
    public boolean cancel;
    public boolean tellStaff;
    public int offset;

    CommandConfig(String commandName, boolean cancel, boolean tellStaff, int offset) {
        this.commandName = commandName;
        this.cancel = cancel;
        this.tellStaff = tellStaff;
        this.offset = offset;
    }

    CommandConfig(String commandName, boolean cancel, boolean tellStaff) {
        this(commandName, cancel, tellStaff, 0);
    }

    CommandConfig(String commandName, boolean cancel) {
        this(commandName, cancel, false);
    }

    CommandConfig(String commandName) {
        this(commandName, false);
    }
}
