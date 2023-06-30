package cc.carm.lib.easyplugin.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class EasyVault {
    public static boolean hasVault() {
        return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
    }

    public static @NotNull EasyVault create() throws Exception {
        return new EasyVault();
    }

    private Economy economy = null;

    public EasyVault() throws Exception {
        if (!setupEconomy()) throw new Exception("Vault not found!");
    }

    public boolean setupEconomy() {
        if (!hasVault()) return false;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        this.economy = rsp.getProvider();
        return true;
    }

    public Economy getEconomy() {
        return economy;
    }

    public double getMoney(@NotNull Player player) {
        try {
            return getEconomy().getBalance(player);
        } catch (NullPointerException ignore) {
            return 0L;
        }
    }

    public double getMoney(@NotNull OfflinePlayer player) {
        try {
            return getEconomy().getBalance(player);
        } catch (NullPointerException ignore) {
            return 0L;
        }
    }

    public boolean checkMoney(@NotNull OfflinePlayer player, double amount) {
        return getMoney(player) >= amount;
    }

    public boolean checkMoney(@NotNull Player player, double amount) {
        return getMoney(player) >= amount;
    }

    public EconomyResponse removeMoney(Player player, double amount) {
        return getEconomy().withdrawPlayer(player, amount);
    }

    public EconomyResponse removeMoney(OfflinePlayer player, double amount) {
        return getEconomy().withdrawPlayer(player, amount);
    }

    public EconomyResponse addMoney(Player player, double amount) {
        return getEconomy().depositPlayer(player, amount);
    }

    public EconomyResponse addMoney(OfflinePlayer player, double amount) {
        return getEconomy().depositPlayer(player, amount);
    }

}