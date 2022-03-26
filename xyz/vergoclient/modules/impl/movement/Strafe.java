package xyz.vergoclient.modules.impl.movement;

import net.minecraft.client.Minecraft;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.ServerUtils;

public class Strafe extends Module implements OnEventInterface {

    public Strafe() {
        super("Strafe", Category.MOVEMENT);
    }

    protected static Minecraft mc = Minecraft.getMinecraft();
    private static int strafe = 1;

    public static NumberSetting range = new NumberSetting("Range", 1, 0, 3, 0.1);

    @Override
    public void onEnable() {

        if (Vergo.config.modDisabler.isDisabled() && ServerUtils.isOnHypixel()) {
            Vergo.config.modDisabler.toggle();
            ChatUtils.addProtMsg("Disabler has been enabled for strafe.");
        }
    }

    @Override
    public void onDisable() {
        // Do nothing
    }

    @Override
    public void loadSettings() {
        addSettings(range);
    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventTick) {
            setInfo("Hypixel");
        }

        if (e instanceof EventMove) {
            EventMove event = (EventMove) e;
            MovementUtils.setSpeed(MovementUtils.getSpeed());
        }
    }

}
