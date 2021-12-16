package xyz.vergoclient.modules.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.settings.SettingChangeEvent;
import xyz.vergoclient.util.MovementUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.BlockPos;


public class ModFly extends Module implements OnEventInterface, OnSettingChangeInterface {

	public ModFly() {
		super("Fly", Category.MOVEMENT);
	}
	
	public ModeSetting mode = new ModeSetting("Mode", "Double jump exploit", "Double jump exploit", "Jetpack", "Verus infinite", "Test");
	public NumberSetting hSpeed = new NumberSetting("H Speed", 0.15, 0.05, 5, 0.05),
			vSpeed = new NumberSetting("V speed", 0.4, 0.05, 3, 0.05);
	
	@Override
	public void loadSettings() {
		
		mode.modes.clear();
		mode.modes.addAll(Arrays.asList("Double jump exploit", "Jetpack", "Verus infinite", "Test"));
		
		addSettings(mode, hSpeed, vSpeed);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		if (e.setting == mode) {


		}
	}
	
	public static transient boolean waiting = false, doubleJumped = false;
	public static transient ArrayList<WorldBlock> verusBlocks = new ArrayList<ModFly.WorldBlock>();
	public static class WorldBlock{
		public WorldBlock(BlockPos pos) {
			this.state = Minecraft.getMinecraft().theWorld.getBlockState(pos);
			this.pos = pos;
		}
		public IBlockState state;
		public BlockPos pos;
	}
	
	@Override
	public void onEnable() {
		
		waiting = false;
		doubleJumped = false;
	}
	
	@Override
	public void onDisable() {

		MovementUtils.setMotion(MovementUtils.getBaseMoveSpeed());
		mc.thePlayer.motionY = 0;
		if (mode.is("Test")) {
//			mc.timer.timerSpeed = 0.01f;
//			new Thread(() -> {
//				try {
//					Thread.sleep(200);
//				} catch (Exception e) {
//					
//				}
//				mc.timer.timerSpeed = 1;
//			}).start();
		}
		
		if (mode.is("Verus infinite")) {
			MovementUtils.setMotion(0);
//			mc.thePlayer.motionY = -0.0784000015258789;
			for (WorldBlock worldBlock : verusBlocks) {
				mc.theWorld.setBlockState(worldBlock.pos, worldBlock.state);
			}
			verusBlocks.clear();
		}
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventTick && e.isPre()) {
			setInfo(mode.getMode());
		}
		
		if (mode.is("Test") && e instanceof EventUpdate && e.isPre()) {
			
			mc.thePlayer.motionY = 0;
			mc.thePlayer.onGround = true;
			
			if (mc.thePlayer.ticksExisted % (MovementUtils.isMoving() ? 2 : 4) == 0) {
				if (mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (vSpeed.getValueAsDouble() * 2) + 2, mc.thePlayer.posZ, true));
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (vSpeed.getValueAsDouble() * 2), mc.thePlayer.posZ);
				}
				else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - (vSpeed.getValueAsDouble() * 2), mc.thePlayer.posZ);
				}
			}else {
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.lastTickPosY, mc.thePlayer.posZ);
			}
			
			MovementUtils.strafe((float) hSpeed.getValueAsDouble());
			if (!MovementUtils.isMoving())
				MovementUtils.setMotion(0);
			
		}
		else if (mode.is("Double jump exploit") && e instanceof EventUpdate && e.isPre()) {
			
			if (mc.thePlayer.capabilities.allowFlying && !waiting) {
				mc.thePlayer.capabilities.isFlying = true;
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(mc.thePlayer.capabilities));
				waiting = true;
				return;
			}
			else if (mc.thePlayer.capabilities.isFlying && !waiting) {
				doubleJumped = true;
			}
			else if ((!mc.thePlayer.capabilities.isFlying && waiting) || !MovementUtils.isOnGround(2)) {
				waiting = false;
				doubleJumped = true;
			}
			else if (mc.thePlayer.capabilities.isFlying && waiting) {
				MovementUtils.strafe(0);
				mc.thePlayer.motionY = 0;
			}
			
			if (doubleJumped) {
				mc.thePlayer.motionY = 0;
				if (mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.thePlayer.motionY = vSpeed.getValueAsDouble();
				}
				else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
					mc.thePlayer.motionY = -vSpeed.getValueAsDouble();
				}
				MovementUtils.strafe((float) hSpeed.getValueAsDouble());
				if (!MovementUtils.isMoving())
					MovementUtils.setMotion(0);
			}
			
		}
		
		if (mode.is("Jetpack") && e instanceof EventTick && e.isPre() && (MovementUtils.isMoving() || mc.gameSettings.keyBindJump.isKeyDown())) {
			MovementUtils.setMotion(MovementUtils.getSpeed() + (MovementUtils.isOnGround(0.5) ? 0 : hSpeed.getValueAsDouble()));
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY = vSpeed.getValueAsDouble();
			}
		}
		
		if (mode.is("Verus infinite")) {
			if (e instanceof EventUpdate && e.isPre()) {
				
				if (player.isLastOnGround() && !player.isOnGround()) {
					mc.thePlayer.motionY = -0.0784000015258789;
				}
				
				if (player.isOnGround()) {
					MovementUtils.setMotion(0.272935);
					mc.thePlayer.jump();
				}
				
				MovementUtils.strafe();
				
				if (!MovementUtils.isMoving())
					MovementUtils.setMotion(0);
				
			}
			else if (e instanceof EventTick && e.isPre()) {
				for (WorldBlock worldBlock : verusBlocks) {
					mc.theWorld.setBlockState(worldBlock.pos, worldBlock.state);
				}
				verusBlocks.clear();
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(-1, -1, -1)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(0, -1, -1)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(1, -1, -1)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(1, -1, 0)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(1, -1, 1)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(0, -1, 0)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(0, -1, 1)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(-1, -1, 1)));
				verusBlocks.add(new WorldBlock(mc.thePlayer.getRealPosition().add(-1, -1, 0)));
				for (WorldBlock worldBlock : verusBlocks) {
//					mc.theWorld.setBlockState(worldBlock.pos, Blocks.glass.getDefaultState());
					mc.theWorld.setBlockState(worldBlock.pos, Blocks.barrier.getDefaultState());
				}
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(-1, -1, -1), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(0, -1, -1), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(1, -1, -1), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(1, -1, 0), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(1, -1, 1), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(0, -1, 0), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(0, -1, 1), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(-1, -1, 1), Blocks.glass.getDefaultState());
//				mc.theWorld.setBlockState(mc.thePlayer.getRealPosition().add(-1, -1, 0), Blocks.glass.getDefaultState());	
			}
		}
		
	}
	
}
