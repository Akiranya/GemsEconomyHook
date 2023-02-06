package me.hsgamer.bettergui.gemsecohook;

import it.unimi.dsi.fastutil.Pair;
import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.account.Account;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.Bukkit;

import java.util.UUID;

public class GemsEconomyHook {

    private static GemsEconomy api;

    private GemsEconomyHook() {
        throw new UnsupportedOperationException();
    }

    public static void setupPlugin() {
        api = (GemsEconomy) Bukkit.getPluginManager().getPlugin("GemsEconomy");
    }

    public static double getBalance(UUID uuid, String currencyId) {
        Currency currency = api.getCurrencyManager().getCurrency(currencyId);
        Account account = api.getAccountManager().fetchAccount(uuid);
        if (hasNullable(Pair.of(uuid, account), Pair.of(currencyId, currency)))
            return -1;
        return account.getBalance(currency);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasBalance(UUID uuid, String currencyId, double minimum) {
        return getBalance(uuid, currencyId) >= minimum;
    }

    public static boolean takeBalance(UUID uuid, String currencyId, double amount) {
        Currency currency = api.getCurrencyManager().getCurrency(currencyId);
        Account account = api.getAccountManager().fetchAccount(uuid);
        if (hasNullable(Pair.of(uuid, account), Pair.of(currencyId, currency)))
            return false;
        account.withdraw(currency, amount);
        return true;
    }

    public static boolean giveBalance(UUID uuid, String currencyId, double amount) {
        Currency currency = api.getCurrencyManager().getCurrency(currencyId);
        Account account = api.getAccountManager().fetchAccount(uuid);
        if (hasNullable(Pair.of(uuid, account), Pair.of(currencyId, currency)))
            return false;
        account.deposit(currency, amount);
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasAccumulatedBalance(UUID uuid, String currencyId, double minimum) {
        Currency currency = api.getCurrencyManager().getCurrency(currencyId);
        Account account = api.getAccountManager().fetchAccount(uuid);
        if (hasNullable(Pair.of(uuid, account), Pair.of(currencyId, currency)))
            return false;
        return account.getAccBalance(currencyId) >= minimum;
    }

    private static boolean hasNullable(Pair<UUID, Account> account, Pair<String, Currency> currency) {
        if (account.right() == null) {
            Main.INSTANCE.severe("Cannot find GemsEconomy account for player: " + account.left());
            return true;
        }
        if (currency.right() == null) {
            Main.INSTANCE.severe("Cannot find GemsEconomy currency with id: " + currency.left());
            return true;
        }
        return false;
    }

}
