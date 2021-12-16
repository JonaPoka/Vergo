package xyz.vergoclient.modules.impl.miscellaneous;

import java.util.concurrent.CopyOnWriteArrayList;

import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventRenderGUI;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import net.minecraft.client.gui.ScaledResolution;

public class ModPacketDebugger extends Module implements OnEventInterface {

	public ModPacketDebugger() {
		super("PacketDebugger", Category.MISCELLANEOUS);
	}
	
	private static CopyOnWriteArrayList<String> lines = new CopyOnWriteArrayList<String>();
	
	public BooleanSetting debugReceivedServerPackets = new BooleanSetting("Debug received server packets", true),
			debugReceivedClientPackets = new BooleanSetting("Debug received client packets", true),
			debugSentServerPackets = new BooleanSetting("Debug sent server packets", true),
			debugSentClientPackets = new BooleanSetting("Debug sent client packets", true);
	
	@Override
	public void loadSettings() {
		addSettings(debugReceivedServerPackets, debugReceivedClientPackets, debugSentServerPackets, debugSentClientPackets);
	}
	
	@Override
	public void onEnable() {
		lines.clear();
	}
	
	@Override
	public void onDisable() {
		lines.clear();
	}
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventSendPacket && e.isPost() && !e.isCanceled() && ((debugSentClientPackets.isEnabled() && ((EventSendPacket)e).packet.getClass().getSimpleName().startsWith("C")) || (debugSentServerPackets.isEnabled() && ((EventSendPacket)e).packet.getClass().getSimpleName().startsWith("S")))) {
			lines.add(0, "SENT: " + ((EventSendPacket)e).packet.getClass().getSimpleName());
			System.out.println("SENT: " + ((EventSendPacket)e).packet.getClass().getSimpleName());
//			ChatUtils.addChatMessage("SENT: " + ((EventSendPacket)e).packet);
		}
		else if (e instanceof EventReceivePacket && e.isPost() && !e.isCanceled() && ((debugReceivedClientPackets.isEnabled() && ((EventReceivePacket)e).packet.getClass().getSimpleName().startsWith("C")) || (debugReceivedServerPackets.isEnabled() && ((EventReceivePacket)e).packet.getClass().getSimpleName().startsWith("S")))) {
			lines.add(0, "RECEIVED: " + ((EventReceivePacket)e).packet.getClass().getSimpleName());
			System.out.println("RECEIVED: " + ((EventReceivePacket)e).packet.getClass().getSimpleName());
//			ChatUtils.addChatMessage("RECEIVED: " + ((EventReceivePacket)e).packet);
		}
		else if (e instanceof EventRenderGUI && e.isPre()) {
			ScaledResolution sr = new ScaledResolution(mc);
			if (lines.size() - 1 >= 99) {
				for (int i = lines.size() - 1 ; i > 99; i--) {
					lines.remove(i);
				}
			}
			float widthBackup = 0;
			for (String text : lines) {
				if (mc.fontRendererObj.getStringWidth(text) > widthBackup)
					widthBackup = mc.fontRendererObj.getStringWidth(text);
			}
			widthBackup += 10;
			int offset = 0;
			for (String text : lines) {
				offset++;
				mc.fontRendererObj.drawString(text, sr.getScaledWidth() - widthBackup, sr.getScaledHeight() - ((mc.fontRendererObj.FONT_HEIGHT) * offset), offset % 2 == 1 ? -1 : 0xff9f9f9f, true);
			}
		}
	}

}
