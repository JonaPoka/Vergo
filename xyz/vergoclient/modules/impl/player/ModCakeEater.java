package xyz.vergoclient.modules.impl.player;

import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.modules.impl.combat.ModKillAura;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.RotationUtils;
import net.minecraft.block.BlockCake;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ModCakeEater extends Module implements OnEventInterface {

	public ModCakeEater() {
		super("CakeEater", Category.PLAYER);
	}

	@Override
	public void onEvent(Event e) {
		
		
		if (e instanceof EventRender3D && e.isPre()) {
			
			CakeInfo info = findCake();
			
			BlockPos cake = info.pos;
			EnumFacing cakeFace = info.face;
			
			if (cake == null || cakeFace == null) {
				return;
			}
			
			if (mc.theWorld.getBlockState(cake.add(0, 1, 0)).getBlock() != Blocks.air) {
				for (int i = 0; i < 5; i++) {
					
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ());
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ());
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ());
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ());
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ());
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ());
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ() + 1, cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1, cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ() + 1, cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1, cake.offset(EnumFacing.UP).getX(), cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ() + 1, cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY() + 1, cake.offset(EnumFacing.UP).getZ() + 1);
					RenderUtils.drawLine(cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ(), cake.offset(EnumFacing.UP).getX() + 1, cake.offset(EnumFacing.UP).getY(), cake.offset(EnumFacing.UP).getZ() + 1);
					
				}
				return;
			}
			
			for (int i = 0; i < 5; i++) {
				
				RenderUtils.drawLine(cake.getX(), cake.getY(), cake.getZ(), cake.getX() + 1, cake.getY(), cake.getZ());
				RenderUtils.drawLine(cake.getX(), cake.getY() + 0.5, cake.getZ(), cake.getX() + 1, cake.getY() + 0.5, cake.getZ());
				RenderUtils.drawLine(cake.getX(), cake.getY(), cake.getZ(), cake.getX(), cake.getY(), cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX(), cake.getY() + 0.5, cake.getZ(), cake.getX(), cake.getY() + 0.5, cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX(), cake.getY(), cake.getZ(), cake.getX(), cake.getY() + 0.5, cake.getZ());
				RenderUtils.drawLine(cake.getX(), cake.getY() + 0.5, cake.getZ(), cake.getX(), cake.getY() + 0.5, cake.getZ());
				RenderUtils.drawLine(cake.getX() + 1, cake.getY(), cake.getZ(), cake.getX() + 1, cake.getY() + 0.5, cake.getZ());
				RenderUtils.drawLine(cake.getX() + 1, cake.getY() + 0.5, cake.getZ(), cake.getX() + 1, cake.getY() + 0.5, cake.getZ());
				RenderUtils.drawLine(cake.getX(), cake.getY(), cake.getZ() + 1, cake.getX(), cake.getY() + 0.5, cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX(), cake.getY() + 0.5, cake.getZ() + 1, cake.getX(), cake.getY() + 0.5, cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX() + 1, cake.getY(), cake.getZ() + 1, cake.getX(), cake.getY(), cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX() + 1, cake.getY() + 0.5, cake.getZ() + 1, cake.getX(), cake.getY() + 0.5, cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX() + 1, cake.getY(), cake.getZ() + 1, cake.getX() + 1, cake.getY() + 0.5, cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX() + 1, cake.getY() + 0.5, cake.getZ(), cake.getX() + 1, cake.getY() + 0.5, cake.getZ() + 1);
				RenderUtils.drawLine(cake.getX() + 1, cake.getY(), cake.getZ(), cake.getX() + 1, cake.getY(), cake.getZ() + 1);
				
			}
			
		}
		
		if (e instanceof EventUpdate && e.isPost()) {
			
			setInfo("Mineplex");
			
			CakeInfo info = findCake();
			
			BlockPos cake = info.pos;
			EnumFacing cakeFace = info.face;
			
			if (cake == null || cakeFace == null) {
				return;
			}
			
			EventUpdate event = (EventUpdate)e;
			
			if ((Vergo.config.modKillAura.isDisabled() || ModKillAura.target == null)) {
				float[] rots = RotationUtils.getRotationFromPosition(cake.getX(), cake.getZ(), cake.getY());
				event.setYaw(rots[0]);
				event.setPitch(rots[1]);
				RenderUtils.setCustomYaw(rots[0]);
				RenderUtils.setCustomPitch(rots[1]);
			}
			
			boolean mineInsteadOfEat = true;
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (mc.theWorld.getBlockState(cake.offset(facing)).getBlock() == Blocks.air){
					mineInsteadOfEat = false;
					cakeFace = facing;
				}
			}
			
			
			if (mineInsteadOfEat) {
				
				if ((Vergo.config.modKillAura.isDisabled() || ModKillAura.target == null)) {
					float[] rots = RotationUtils.getRotationFromPosition(cake.add(0, 1, 0).getX(), cake.add(0, 1, 0).getZ(), cake.add(0, 1, 0).getY());
					event.setYaw(rots[0]);
					event.setPitch(rots[1]);
					RenderUtils.setCustomYaw(rots[0]);
					RenderUtils.setCustomPitch(rots[1]);
				}
				
				mc.playerController.curBlockDamageMP += 0.05f;
				mc.thePlayer.swingItem();
				mc.playerController.onPlayerDamageBlock(cake.add(0, 1, 0), EnumFacing.UP);
				return;
			}
			
//			mc.playerController.curBlockDamageMP = 1.0f;
//			mc.playerController.onPlayerDamageBlock(bed, bedFace);
			float[] rots = RotationUtils.getRotationFromPosition(cake.getX(), cake.getZ(), cake.getY());
			mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), cake, EnumFacing.UP, RotationUtils.getVectorForRotation(rots[0], rots[1]));
			
		}
		
		
	}
	
	public static CakeInfo lastCake = null;
	
	public static CakeInfo findCake() {
		
		try {
			
			if (lastCake != null && mc.thePlayer.getDistance(lastCake.pos.getX(), lastCake.pos.getY(), lastCake.pos.getZ()) <= 6) {
				if (mc.theWorld.getBlockState(lastCake.pos).getBlock() instanceof BlockCake) {
					return lastCake;
				}
			}
			
		} catch (Exception e) {
			
		}
		
		BlockPos cake = null;
		EnumFacing cakeFace = null;
		
		for (EnumFacing face1 : EnumFacing.VALUES) {
			
			BlockPos playerPos = mc.thePlayer.getPosition();
			if (mc.theWorld.getBlockState(playerPos.offset(face1)).getBlock() instanceof BlockCake) {
				
				cake = playerPos.offset(face1);
				cakeFace = face1.getOpposite();
				break;
				
			}
			
			for (EnumFacing face2 : EnumFacing.VALUES) {
				
				BlockPos pos2 = playerPos.offset(face2);
				
				if (mc.theWorld.getBlockState(pos2).getBlock() instanceof BlockCake) {
					
					cake = pos2;
					cakeFace = face2.getOpposite();
					break;
					
				}
				
				for (EnumFacing face3 : EnumFacing.VALUES) {
					
					BlockPos pos3 = pos2.offset(face3);
					
					if (mc.theWorld.getBlockState(pos3).getBlock() instanceof BlockCake) {
						
						cake = pos3;
						cakeFace = face3.getOpposite();
						break;
						
					}
					
					for (EnumFacing face4 : EnumFacing.VALUES) {
						
						BlockPos pos4 = pos3.offset(face4);
						
						if (mc.theWorld.getBlockState(pos4).getBlock() instanceof BlockCake) {
							
							cake = pos4;
							cakeFace = face4.getOpposite();
							break;
							
						}
						
						for (EnumFacing face5 : EnumFacing.VALUES) {
							
							BlockPos pos5 = pos4.offset(face5);
							
							if (mc.theWorld.getBlockState(pos5).getBlock() instanceof BlockCake) {
								
								cake = pos5;
								cakeFace = face5.getOpposite();
								break;
								
							}
							
//							for (EnumFacing face6 : EnumFacing.VALUES) {
//								
//								BlockPos pos6 = pos5.offset(face6);
//								
//								if (mc.theWorld.getBlockState(pos6).getBlock() instanceof BlockCake) {
//									
//									cake = pos6;
//									cakeFace = face6.getOpposite();
//									break;
//									
//								}
//								
//								for (EnumFacing face7 : EnumFacing.VALUES) {
//									
//									BlockPos pos7 = pos6.offset(face7);
//									
//									if (mc.theWorld.getBlockState(pos7).getBlock() instanceof BlockCake) {
//										
//										cake = pos7;
//										cakeFace = face7.getOpposite();
//										break;
//										
//									}
//									
//								}
//								
//							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		lastCake = new CakeInfo(cake, cakeFace);
		return lastCake;
		
		
	}
	
	public static class CakeInfo {
		
		public CakeInfo(BlockPos pos, EnumFacing face) {
			this.pos = pos;
			this.face = face;
		}
		
		public BlockPos pos;
		public EnumFacing face;
		
	}
	
}
