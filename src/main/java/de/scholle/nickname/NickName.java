package de.scholle.nickname;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class NickName extends JavaPlugin implements CommandExecutor {

    private final Map<String, String> nicknames = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultLanguageFiles();
        getCommand("nickname").setExecutor(this);
    }

    private void saveDefaultLanguageFiles() {
        File langFolder = new File(getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();
        saveResource("lang/en_US.json", false);
        saveResource("lang/de_DE.json", false);
    }

    public void onEnable_OLD() {
        getCommand("nickname").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Benutzung: /nickname <set|force|namecheck|nicknamecheck> ...");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "set":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
                    return true;
                }
                if (!sender.hasPermission("nickname.set")) {
                    sender.sendMessage(ChatColor.RED + "Keine Berechtigung!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Benutzung: /nickname set <nickname>");
                    return true;
                }
                Player self = (Player) sender;
                setNickname(self, args[1]);
                sender.sendMessage(ChatColor.GREEN + "Dein Nickname wurde zu " + args[1] + " geändert.");
                break;

            case "force":
                if (!sender.hasPermission("nickname.force")) {
                    sender.sendMessage(ChatColor.RED + "Keine Berechtigung!");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Benutzung: /nickname force <player> <nickname>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
                    return true;
                }
                setNickname(target, args[2]);
                sender.sendMessage(ChatColor.GREEN + "Nickname von " + target.getName() + " geändert zu " + args[2] + ".");
                break;

            case "namecheck":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Benutzung: /nickname namecheck <player>");
                    return true;
                }
                Player check1 = Bukkit.getPlayer(args[1]);
                if (check1 == null) {
                    sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
                    return true;
                }
                String nickname = nicknames.getOrDefault(check1.getName(), check1.getName());
                sender.sendMessage(ChatColor.YELLOW + "Nickname von " + check1.getName() + ": " + nickname);
                break;

            case "nicknamecheck":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Benutzung: /nickname nicknamecheck <player>");
                    return true;
                }
                Player check2 = Bukkit.getPlayer(args[1]);
                if (check2 == null) {
                    sender.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
                    return true;
                }
                sender.sendMessage(ChatColor.YELLOW + "Echter Name von " + args[1] + ": " + check2.getName());
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Unbekannter Befehl.");
        }
        return true;
    }

    private void setNickname(Player player, String nickname) {
        nicknames.put(player.getName(), nickname);
        player.setDisplayName(nickname);
        player.setPlayerListName(nickname);
        applyNameTag(player, nickname);
    }

    public String getNickname(Player p) {
        return nicknames.getOrDefault(p.getName(), p.getName());
    }

    private void applyNameTag(Player player, String nickname) {
        org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "nick_" + player.getName();
        if (board.getTeam(teamName) != null) board.getTeam(teamName).unregister();
        org.bukkit.scoreboard.Team team = board.registerNewTeam(teamName);
        team.setPrefix("");
        team.setSuffix("");
        team.addEntry(player.getName());
        team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.ALWAYS);
        player.setScoreboard(board);
    }
}
