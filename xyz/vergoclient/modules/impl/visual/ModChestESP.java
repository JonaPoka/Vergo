package xyz.vergoclient.modules.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventRender3D;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.BlockPos;

public class ModChestESP extends Module implements OnEventInterface {

	public ModChestESP() {
		super("ChestESP", Category.VISUAL);
	}
	
	public NumberSetting normalRed = new NumberSetting("Normal red", 255, 0, 255, 1),
			normalGreen = new NumberSetting("Normal green", 105, 0, 255, 1),
			normalBlue = new NumberSetting("Normal blue", 180, 0, 255, 1),
			normalAlpha = new NumberSetting("Normal alpha", 0, 0, 255, 1),
			throughWallsRed = new NumberSetting("Walls red", 255, 0, 255, 1),
			throughWallsGreen = new NumberSetting("Walls green", 105, 0, 255, 1),
			throughWallsBlue = new NumberSetting("Walls blue", 180, 0, 255, 1),
			throughWallsAlpha = new NumberSetting("Walls alpha", 50, 0, 255, 1);
	
	@Override
	public void loadSettings() {
		addSettings(normalRed, normalGreen, normalBlue, normalAlpha);
	}
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventRender3D && e.isPre()) {
			for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
				if (tileEntity instanceof TileEntityChest) {
					TileEntityLockable storage = (TileEntityLockable) tileEntity;
					BlockPos chestLocation = storage.getPos();
					
					GlStateManager.pushMatrix();
					GlStateManager.pushAttrib();
					
					GlStateManager.depthMask(false);
					
					GL11.glClearStencil(0);
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					GL11.glEnable(GL11.GL_STENCIL_TEST);
					GL11.glStencilFunc(GL11.GL_ALWAYS, 1, -1);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
					GL11.glEnable(32823);
					GL11.glPolygonOffset(1.0f, -1099998.0f);
					RenderUtils.drawColoredBox(chestLocation.getX() - 0.0001, chestLocation.getY() - 0.0001, chestLocation.getZ() - 0.0001, chestLocation.getX() + 1.0001, chestLocation.getY() + 1.0001, chestLocation.getZ() + 1.0001, new Color(throughWallsRed.getValueAsInt(), throughWallsGreen.getValueAsInt(), throughWallsBlue.getValueAsInt(), throughWallsAlpha.getValueAsInt()).getRGB());
					GL11.glDisable(32823);
					GL11.glPolygonOffset(1.0f, 1099998.0f);
					
					GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, -1);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
					GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
					RenderUtils.drawColoredBox(chestLocation.getX() - 0.0001, chestLocation.getY() - 0.0001, chestLocation.getZ() - 0.0001, chestLocation.getX() + 1.0001, chestLocation.getY() + 1.0001, chestLocation.getZ() + 1.0001, 0x00000000);
					RenderUtils.drawColoredBox(chestLocation.getX() - 0.0001, chestLocation.getY() - 0.0001, chestLocation.getZ() - 0.0001, chestLocation.getX() + 1.0001, chestLocation.getY() + 1.0001, chestLocation.getZ() + 1.0001, new Color(normalRed.getValueAsInt(), normalGreen.getValueAsInt(), normalBlue.getValueAsInt(), normalAlpha.getValueAsInt()).getRGB());
					
					GlStateManager.depthMask(true);
					
					GlStateManager.popAttrib();
					GlStateManager.popMatrix();
					
				}
			}
		}
	}

}
