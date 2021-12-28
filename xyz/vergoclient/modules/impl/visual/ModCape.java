package xyz.vergoclient.modules.impl.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import xyz.vergoclient.files.FileManager;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnSettingChangeInterface;
import xyz.vergoclient.settings.FileSetting;
import xyz.vergoclient.settings.SettingChangeEvent;

import javax.imageio.ImageIO;

public class ModCape extends Module implements OnSettingChangeInterface {

    public ModCape() {
        super("Cape", Category.VISUAL);
    }

    public FileSetting capeFile = new FileSetting("Cape file", FileManager.capesDir);

    @Override
    public void loadSettings() {
        addSettings(capeFile);
    }

    public static transient ResourceLocation capeLocation = null;

    @Override
    public void onSettingChange(SettingChangeEvent e) {
        if (e.setting == capeFile) {
            try {
                if (capeFile.is("None")) {
//					ChatUtils.addChatMessage("Failed to load that image file");
                    return;
                }
                capeLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(capeFile.getFile().getAbsolutePath(), new DynamicTexture(ImageIO.read(capeFile.getFile())));
            } catch (Exception e2) {
//				ChatUtils.addChatMessage("Failed to load that image file");
            }
        }
    }

}
