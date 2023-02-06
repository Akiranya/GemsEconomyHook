package me.hsgamer.bettergui.gemsecohook.requirement;

import it.unimi.dsi.fastutil.Pair;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.TakableRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.gemsecohook.GemsEconomyHook;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class BalanceRequirement extends TakableRequirement<Pair<String, Double>> {
    public BalanceRequirement(final RequirementBuilder.Input input) {
        super(input);
        getMenu().getVariableManager().register(getName(), (original, uuid) -> {
            Pair<String, Double> test = getFinalValue(uuid);
            if (test.right() > 0 && !GemsEconomyHook.hasBalance(uuid, test.left(), test.right())) {
                return test.right().toString();
            }
            return BetterGUI.getInstance().getMessageConfig().haveMetRequirementPlaceholder;
        });
    }

    @Override protected Pair<String, Double> convert(final Object value, final UUID uuid) {
        String parsed = StringReplacerApplier.replace(String.valueOf(value).trim(), uuid, this);
        String[] parts = parsed.split("/", 2);
        String currencyId = parts[1];
        double amount = Validate.getNumber(parts[0])
            .map(BigDecimal::doubleValue)
            .orElseGet(() -> {
                Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> MessageUtils.sendMessage(player, BetterGUI.getInstance().getMessageConfig().invalidNumber.replace("{input}", parsed)));
                return 0D;
            });
        return Pair.of(currencyId, amount);
    }

    @Override protected Result checkConverted(final UUID uuid, final Pair<String, Double> value) {
        final double amount = value.right();
        final String currencyId = value.left();
        if (amount > 0D && !GemsEconomyHook.hasBalance(uuid, currencyId, amount)) {
            return Result.fail();
        } else {
            return successConditional((uuid1, process) -> Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
                if (!GemsEconomyHook.takeBalance(uuid1, currencyId, amount)) {
                    Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendRichMessage("<red>Error: the transaction couldn't be executed. Please inform the staff."));
                }
                process.next();
            }));
        }
    }

    @Override protected boolean getDefaultTake() {
        return true;
    }

    @Override protected Object getDefaultValue() {
        return "0/R";
    }
}
