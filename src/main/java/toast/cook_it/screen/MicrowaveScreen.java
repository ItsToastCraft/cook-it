package toast.cook_it.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;

public class MicrowaveScreen extends HandledScreen<MicrowaveScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(CookIt.MOD_ID, "textures/gui/microwave.png");
    public MicrowaveScreen(MicrowaveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    @Override
    protected void init() {
        super.init();
        //titleY = 1000;
        playerInventoryTitleY = 1000;


    }
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width-backgroundWidth) / 2;
        int y = (height-backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderProgressThingy(context, x,  y);
        renderDigit1(context, x,  y);
        renderDigit2(context, x,  y);
    }

    private void renderProgressThingy(DrawContext context, int x, int y) {
        if (handler.isCrafting()) {

            context.drawTexture(TEXTURE, x + 85, y + 30, 176, 0, 8, handler.getScaledProgress());
        }
    }
    private void renderDigit1(DrawContext context, int x, int y) {
        if (handler.isCrafting()) {
            context.drawTexture(TEXTURE, x + 140, y + 30, 176, 26+10*handler.getDigit1(), 8, 12);
        }
    }

    private void renderDigit2(DrawContext context, int x, int y) {
        if (handler.isCrafting()) {
            context.drawTexture(TEXTURE, x + 150, y + 30, 176, 26+10*handler.getDigit2(), 8, 12);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
