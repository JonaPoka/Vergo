package xyz.vergoclient.ui.guis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import xyz.vergoclient.assets.Colors;
import xyz.vergoclient.assets.Icons;
import xyz.vergoclient.ui.fonts.FontUtil;
import xyz.vergoclient.ui.fonts.JelloFontRenderer;
import xyz.vergoclient.util.MiscellaneousUtils;
import xyz.vergoclient.util.NetworkManager;
import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class GuiYiffViewer extends GuiScreen {
	
	private static GuiYiffViewer yiffGui = new GuiYiffViewer();
	public static GuiYiffViewer getYiffGui() {
		refreshYiff = true;
		return yiffGui;
	}
	
	public static CopyOnWriteArrayList<Yiff> yiffs = new CopyOnWriteArrayList<>();
	public static boolean refreshYiff = false;
	public static TimerUtil e621RateLimiter = new TimerUtil();
	
	public static class Yiff {
		public Yiff(String url) {
			
			// Texture manager does't work correctly unless I do this
			yiff = new ResourceLocation("yiffViewer-" + MiscellaneousUtils.getRandomString(10) + "-" + System.nanoTime());
			
			TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
			ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData((File) null, url,
					Icons.MISSING_TEXTURE.iconLocation, new ImageBufferDownload());
			try {
				
				if (!e621RateLimiter.hasTimeElapsed(750, false)) {
					Thread.sleep(System.currentTimeMillis() - e621RateLimiter.lastMS);
				}
				URLConnection connection = new URL(url).openConnection();
				
				// Changed since the e621 api documentation states that you should use a user agent that allows them to identify you
//				connection.setRequestProperty("User-Agent",
//						"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				connection.setRequestProperty("User-Agent", "BrOwOwser; A yiff viewer mod for Minecraft/1.0 (Code by NathanKassab and gui design by imAETHER on github.com)");
				threadDownloadImageData.setBufferedImage(ImageIO.read(connection.getInputStream()));
				texturemanager.loadTexture(yiff, threadDownloadImageData);
				connection.getInputStream().close();
				e621RateLimiter.reset();
			} catch (Exception e) {
				e.printStackTrace();
			}
			texturemanager.loadTexture(yiff, threadDownloadImageData);
		}
		public ResourceLocation yiff = null;
		public double width = 0, height = 0;
		public JSONObject jsonObject = null;
	}
	
	public static void refreshYiff() {
		
		new Thread(() -> {
			String request = "";
			try {
				request = NetworkManager.getNetworkManager().sendGet(new HttpGet("https://e621.net/posts.json?limit=100"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ArrayList<String> posts = new ArrayList<>();
			
			for (int i = 0; i < 25; i++) {
				try {
					
					// Because the e621 api documentation states that you shouldn't send more than 1 request per second over a sustained period of time
					if (!e621RateLimiter.hasTimeElapsed(750, false)) {
						Thread.sleep(System.currentTimeMillis() - e621RateLimiter.lastMS);
					}
					
					// Gets and downloads full image
//					JSONObject json = new JSONObject(new JSONObject(new JSONObject(threadSafeRequest).toString()).getJSONArray("posts").get(threadSafeI).toString()).getJSONObject("file");
					JSONObject json = new JSONObject(new JSONObject(new JSONObject(request).toString()).getJSONArray("posts").get(i).toString());
					if (json.getJSONObject("preview") == null || json.getJSONObject("file") == null) {
						i--;
						continue;
					}
					Yiff yiff = new Yiff(json.getJSONObject("preview").getString("url"));
					yiff.width = json.getJSONObject("preview").getInt("width");
					yiff.height = json.getJSONObject("preview").getInt("height");
					yiff.jsonObject = json;
					yiffs.add(yiff);
					e621RateLimiter.reset();
					
				} catch (Exception e) {
					
				}
			}
		}).start();
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		if (refreshYiff) {
			refreshYiff = false;
			yiffs.clear();
			refreshYiff();
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		
		// Window title
		String windowTitle = "BrOwOwser 1.0";
		
		// The font renderers
		JelloFontRenderer arialFr = FontUtil.arialBig;
		JelloFontRenderer arialFrNormal = FontUtil.arialRegular;
		JelloFontRenderer arialFrSmall = FontUtil.arialSmall;
		
		// Draw the browser window
		drawRect(50, 50, width - 50, height - 50, -1);
		drawRect(50, 50, width - 50, 69, 0xff5b5b5b);
		drawRect(70 + arialFr.getStringWidth(windowTitle), 52.5, width - 70, 66.5, 0xff2c2c2c);
		arialFr.drawString(windowTitle, 55, 55, -1);
		arialFrNormal.drawString("Tags...", 74 + arialFr.getStringWidth(windowTitle), 57, 0xffb2b2b2);
		RenderUtils.glColorWithInt(Colors.RED.getColor());
		mc.getTextureManager().bindTexture(Icons.CIRCLE.iconLocation);
		Gui.drawModalRectWithCustomSizedTexture(width - 64, 54, 0, 0, 10, 10, 10, 10);
		
		GlStateManager.color(1, 1, 1, 1);
//		System.out.println(yiffs.get(1).jsonObject);
		
		double widthCalc = (width - 110) / 5;
		double heightCalc = (height - 200) / 5;
		
		GlStateManager.pushMatrix();
		for (Yiff yiff : yiffs) {
			
			double scale = 1;
			scale = widthCalc / yiff.width;
			if (heightCalc / yiff.height< scale) {
				scale = heightCalc / yiff.height;
			}
			
//			System.out.println(yiff.width + " " + yiff.width + " " + widthCalc + " " + heightCalc + " " + scale);
			
			GlStateManager.color(1, 1, 1, 1);
			mc.getTextureManager().bindTexture(yiff.yiff);
			Gui.drawModalRectWithCustomSizedTexture((int) (55 + ((widthCalc - (yiff.width * scale)) / 2)), (int) (74 + ((heightCalc - (yiff.height * scale)) / 2)), 0, 0, (int) (yiff.width * scale), (int) (yiff.height * scale), (float) (yiff.width * scale), (float) (yiff.height * scale));
			arialFrSmall.drawCenteredString("<Post Title>", (float) (55 + ((widthCalc - (yiff.width * scale)) / 2) + ((yiff.width * scale) / 2)), (float) (74 + ((heightCalc - (yiff.height * scale)) / 2) + (yiff.height * scale) + arialFrSmall.getHeight() + 2), 0xff000000);
			
			GlStateManager.translate(widthCalc, 0, 0);
			
			// So it creates rows
			if ((yiffs.indexOf(yiff) + 1) % 5 == 0) {
				GlStateManager.translate(-(widthCalc * 5), heightCalc + 12, 0);
			}
			
		}
		GlStateManager.popMatrix();
		
		if (yiffs.isEmpty()) {
			arialFr.drawCenteredString("Loading...", 50 + ((width - 100) / 2), 50 + ((height - 119) / 2) + arialFr.getHeight(), 0xffb2b2b2);
		}
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
}
