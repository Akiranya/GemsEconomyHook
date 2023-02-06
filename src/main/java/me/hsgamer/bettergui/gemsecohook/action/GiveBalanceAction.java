package me.hsgamer.bettergui.gemsecohook.action;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.gemsecohook.GemsEconomyHook;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class GiveBalanceAction extends BaseAction {

    public GiveBalanceAction(final ActionBuilder.Input input) {
        super(input);
    }

    @Override public void accept(final UUID uuid, final TaskProcess process) {
        String parsed = getReplacedString(uuid);
        String currencyId = input.getOptionAsList().get(0);
        Optional<Double> optionalPoint = Validate.getNumber(parsed).map(BigDecimal::doubleValue);
        if (optionalPoint.isEmpty()) {
            Optional.ofNullable(Bukkit.getPlayer(uuid))
                .ifPresent(player -> player.sendRichMessage("<red>Invalid currency amount: " + parsed));
            process.next();
            return;
        }
        double balanceToGive = optionalPoint.get();
        if (balanceToGive > 0) {
            Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
                if (!GemsEconomyHook.giveBalance(uuid, currencyId, balanceToGive)) {
                    Optional.ofNullable(Bukkit.getPlayer(uuid))
                        .ifPresent(player -> player.sendRichMessage("<red>Error: the transaction couldn't be executed. Please inform the staff."));
                }
                process.next();
            });
        } else {
            process.next();
        }
    }

}
