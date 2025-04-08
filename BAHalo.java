package ui.aurora.gzhwa.wbbb.draw;

import ui.aurora.Client;
import ui.aurora.gzhwa.wbbb.draw.Render3DEvent;
import ui.aurora.gzhwa.Category;
import ui.aurora.gzhwa.Module;
import ui.aurora.gzhwa.settings.wbbb.BooleanSetting;
import ui.aurora.gzhwa.settings.wbbb.ModeSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

public class BAHalo extends Module {

    private final ModeSetting character = new ModeSetting("Character", "azusa", "azusa", "hoshino", "reisa", "shiroko");
    private final BooleanSetting firstPerson = new BooleanSetting("Show in first person", true);
    private final BooleanSetting allPlayers = new BooleanSetting("All players", false);

    private final ResourceLocation hat1 = new ResourceLocation("Aurora/ba/azusa.png");
    private final ResourceLocation hat2 = new ResourceLocation("Aurora/ba/hoshino.png");
    private final ResourceLocation hat3 = new ResourceLocation("Aurora/ba/reisa.png");
    private final ResourceLocation hat4 = new ResourceLocation("Aurora/ba/shiroko.png");

    public BAHalo() {
        super("BAHalo", Category.RENDER, "epic hat");
        this.addSettings(character, firstPerson, allPlayers);
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (!firstPerson.isEnabled() && mc.gameSettings.thirdPersonView == 0) return;

        float partialTicks = event.getTicks();
        double renderPosX = mc.getRenderManager().renderPosX,
                renderPosY = mc.getRenderManager().renderPosY,
                renderPosZ = mc.getRenderManager().renderPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.color(1, 1, 1, 1);

        if (allPlayers.isEnabled()) {
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (player == null || player.isDead || player.isInvisible()) continue;
                renderHat(player, partialTicks, renderPosX, renderPosY, renderPosZ);
            }
        } else {
            EntityPlayer player = mc.thePlayer;
            if (player != null && !player.isDead && !player.isInvisible()) {
                renderHat(player, partialTicks, renderPosX, renderPosY, renderPosZ);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void renderHat(EntityPlayer player, float partialTicks, double renderPosX, double renderPosY, double renderPosZ) {
        double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - renderPosX,
                posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - renderPosY,
                posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - renderPosZ;

        double hatHeight = player.getEntityBoundingBox().maxY - player.getEntityBoundingBox().minY + 0.2;
        double hatSize = 0.5;

        mc.getTextureManager().bindTexture(getHatTexture());

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY + hatHeight, posZ);

        GlStateManager.rotate(-player.rotationYawHead, 0, 1, 0);
        GlStateManager.rotate(mc.getRenderManager().playerViewY, 0, 1, 0);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3d(-hatSize, 0, -hatSize);
        glTexCoord2f(1, 0); glVertex3d(hatSize, 0, -hatSize);
        glTexCoord2f(1, 1); glVertex3d(hatSize, 0, hatSize);
        glTexCoord2f(0, 1); glVertex3d(-hatSize, 0, hatSize);
        glEnd();

        GlStateManager.popMatrix();
    }

    private ResourceLocation getHatTexture() {
        switch (character.getMode()) {
            case "hoshino": return hat2;
            case "reisa": return hat3;
            case "shiroko": return hat4;
            default: return hat1; // default SB
        }
    }
}