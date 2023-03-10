package me.hsgamer.bettergui.gemsecohook;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.gemsecohook.action.GiveBalanceAction;
import me.hsgamer.bettergui.gemsecohook.requirement.AccBalanceRequirement;
import me.hsgamer.bettergui.gemsecohook.requirement.BalanceRequirement;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;
import me.hsgamer.hscore.variable.VariableManager;

public final class Main extends PluginAddon {
    public static Main INSTANCE;

    @Override public void onEnable() {
        GemsEconomyHook.setupPlugin();
        RequirementBuilder.INSTANCE.register(AccBalanceRequirement::new, "gems-acc-balance");
        RequirementBuilder.INSTANCE.register(BalanceRequirement::new, "gems-balance");
        ActionBuilder.INSTANCE.register(GiveBalanceAction::new, "gems-give");
        VariableManager.register("gems_", (original, uuid) -> String.valueOf(GemsEconomyHook.getBalance(uuid, original)));
    }

    @Override public void onDisable() {

    }

    public void warning(String msg) {
        getPlugin().getLogger().warning(msg);
    }

    public void severe(String msg) {
        getPlugin().getLogger().severe(msg);
    }

}
