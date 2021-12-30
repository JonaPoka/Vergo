package xyz.vergoclient.modules.impl.movement;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.Timer;
import xyz.vergoclient.util.TimerUtil;

import java.util.Arrays;
import java.util.Random;

public class ModLongJump extends Module implements OnEventInterface {

    Timer timer;


    public ModLongJump() {
        super("LongJump", Category.MOVEMENT);
        this.timer = new Timer();
    }

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel Bow", "Hypixel Bow", "Velocity Test");

    public NumberSetting speedSlider = new NumberSetting("SpeedSlider", 0.3, 0, 3.0, 0.01), heightSlider = new NumberSetting("Height Slider", 1.99, 1.0, 3.0, 0.01);

    public BooleanSetting automated = new BooleanSetting("Automated", false), autoMove = new BooleanSetting("Auto Move", false),
                          autoJump = new BooleanSetting("Auto Jump", false);
    public static transient TimerUtil hypixelTimer = new TimerUtil();

    @Override
    public void loadSettings() {
        mode.modes.clear();
        mode.modes.addAll(Arrays.asList("Hypixel Bow", "Hypixel Test"));

        addSettings(mode, speedSlider, heightSlider, automated, autoMove, autoJump);
    }

    public int i;
    public int slotId;
    public int ticks;
    public boolean hasHurt = false;

    public ItemStack itemStack = null;

    @Override
    public void onEnable() {


        mc.gameSettings.keyBindSprint.pressed = false;
        mc.gameSettings.keyBindForward.pressed = false;

        if (mode.is("Hypixel Bow")) {
            mode.setMode("Hypixel Test");
        } else if(mode.is("Hypixel Test")) {
            setInfo("Hypickle New");
        }

        this.timer.reset();

        if(MovementUtils.isMoving()) {
            mc.thePlayer.movementInput.moveForward = 0.0f;
            mc.thePlayer.movementInput.moveStrafe = 0.0f;
            MovementUtils.setMotion(0.0f);
        }

        for (i = 0; i < 9; i++) {
            itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemBow)
                break;
        }
        if (i == 9) {
            ChatUtils.addChatMessage("Did not find a bow in your hotbar.");
            toggle();
            return;
        }

        else {
            slotId = mc.thePlayer.inventory.currentItem;
            if (i != slotId) {
                //ChatUtils.addChatMessage("Switching slot from " + slotId + " to " + i);
                mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(i));
            }
            ticks = mc.thePlayer.ticksExisted;
            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(itemStack));
        }
    }

    @Override
    public void onDisable() {

        mc.timer.timerSpeed = 1.0f;

        jumpCount = 0;

        mc.gameSettings.keyBindForward .pressed = false;
        mc.gameSettings.keyBindSprint.pressed = false;

    }

    public boolean veloWasOn = false;

    public int jumpCount = 0;

    public float oldPitch;

    Random r = new Random();
    float random = -88.500000f + r.nextFloat() * (-90.000000f - -89.000000f);

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventUpdate && e.isPre()) {
            //Aim up and shoot shoot shoot!
            if (mode.is("Hypixel Bow")) {
                if (!hasHurt) {

                    if (mc.thePlayer.ticksExisted - ticks == 3) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, random, true));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));

                        // Switch back to original slot
                        if (i != slotId) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slotId));
                        }

                    }
                }

                if(mc.thePlayer.hurtTime == 9) {
                    hasHurt = true;
                }

                if(hasHurt) {

                    if(automated.isEnabled()) {
                        if(autoMove.isEnabled()) {
                            mc.thePlayer.setSprinting(true);
                            mc.thePlayer.movementInput.moveForward = 1;
                        }
                        if(autoJump.isEnabled()) {
                            mc.thePlayer.jump();
                        }
                    }

                    mc.thePlayer.motionY *= heightSlider.getValueAsDouble();

                    int x = 0;
                    while(x < 5) {
                        ChatUtils.addChatMessage(mc.thePlayer.motionY);
                        x++;
                    }
                    MovementUtils.setMotion(MovementUtils.getSpeed() * speedSlider.getValueAsDouble());

                    hasHurt = false;

                    mc.thePlayer.movementInput.moveForward = 0;


                }

            } else if(mode.is("Hypixel Test"))  {

                setInfo("Hypixel LowJump");

                if (!hasHurt) {

                    if (mc.thePlayer.ticksExisted - ticks == 3) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, random, true));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));

                        // Switch back to original slot
                        if (i != slotId) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slotId));
                        }

                        this.timer.reset();

                    }
                }

                if(mc.thePlayer.hurtTime == 9) {
                    hasHurt = true;
                }

                if(this.timer.delay(1000L)) {
                    mc.timer.timerSpeed = 0.8f;
                }

                if(this.timer.delay(1200L)) {
                    mc.timer.timerSpeed = 1.0f;
                }

                if(this.timer.delay(1500L)) {
                    toggle();
                    mc.gameSettings.keyBindForward.pressed = false;
                }

                if(hasHurt) {

                    if(automated.isEnabled()) {

                        if(autoMove.isEnabled()) {
                            mc.gameSettings.keyBindForward.pressed = true;
                            mc.gameSettings.keyBindSprint.pressed = true;
                        }

                        if(autoJump.isEnabled()) {
                            if(mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindJump.isPressed()) {

                            }
                            if(jumpCount == 0) {
                                mc.thePlayer.jump();
                                jumpCount++;
                            } else {

                            }
                        }

                    }

                    MovementUtils.setMotion(speedSlider.getValueAsDouble());

                    if(this.timer.delay(1000L)) {
                        hasHurt = false;
                        jumpCount = 0;
                    }

                }

            }



        }
    }

}