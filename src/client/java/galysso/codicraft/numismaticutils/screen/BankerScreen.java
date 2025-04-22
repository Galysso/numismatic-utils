package galysso.codicraft.numismaticutils.screen;

import com.glisco.numismaticoverhaul.ModComponents;
import com.mojang.blaze3d.systems.RenderSystem;
import galysso.codicraft.numismaticutils.NumismaticUtilsMain;
import galysso.codicraft.numismaticutils.screen.buttons.DynamicButton;
import galysso.codicraft.numismaticutils.utils.NumismaticDraw;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BankerScreen extends HandledScreen<BankerScreenHandler> {
    @Override protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}

    // Texture files
    private static final Identifier GUI_TEXTURE = NumismaticUtilsMain.identifier("textures/gui/banker.png");
    public static final Identifier ICONS_TEXTURE =  NumismaticUtilsMain.identifier("textures/gui/buttons.png");

    // TransferFactor
    int transferFactor;

    // Buttons
    ArrayList<DynamicButton> buttons = new ArrayList<>();
    private DynamicButton goldFromMainToPouchButton;
    private DynamicButton silverFromMainToPouchButton;
    private DynamicButton bronzeFromMainToPouchButton;
    private DynamicButton goldFromPouchToMainButton;
    private DynamicButton silverFromPouchToMainButton;
    private DynamicButton bronzeFromPouchToMainButton;
    private DynamicButton fullFromMainToPouchButton;
    private DynamicButton fullFromPouchToMainButton;
    private DynamicButton goldFromOtherToMainButton;
    private DynamicButton silverFromOtherToMainButton;
    private DynamicButton bronzeFromOtherToMainButton;
    private DynamicButton goldFromMainToOtherButton;
    private DynamicButton silverMainToOtherButton;
    private DynamicButton bronzeMainToOtherButton;
    private DynamicButton fullFromOtherToMainButton;
    private DynamicButton fullFromMainToOtherButton;
    private DynamicButton displayPlayersButton;
    private DynamicButton displayAccountsButton;
    private DynamicButton displayInfoButton;
    private DynamicButton displayTransfertButton;
    private DynamicButton displaySettingsButton;
    private DynamicButton addAccountButton;

    public BankerScreen(BankerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 256;
        backgroundHeight = 256;
    }

    protected void init() {
        this.x = (width - backgroundWidth) / 2;
        this.y = (height - backgroundHeight) / 2;
        buttons.add(addDrawableChild(goldFromMainToPouchButton = new DynamicButton(
            this.x + 70, this.y + 17, 8, 10, 0, 0,
            Text.literal("TEST"),
            button ->  {
                System.out.println("Ouch!");
                // Replace this by a dedicated function
                ModComponents.CURRENCY.get(client.player).silentModify(transferFactor*10000L);
            }
        )));
        buttons.add(addDrawableChild(silverFromMainToPouchButton = new DynamicButton(
            this.x + 70, this.y + 28, 8, 10, 0, 0,
            Text.literal("TEST"),
            button ->  {
                System.out.println("Aïe!");
            }
        )));
        buttons.add(addDrawableChild(bronzeFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 39, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(goldFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Ouch!");
                    ModComponents.CURRENCY.get(client.player).silentModify(-10000);
                }
        )));
        buttons.add(addDrawableChild(silverFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 28, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Aïe!");
                }
        )));
        buttons.add(addDrawableChild(bronzeFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 39, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(fullFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 17, 8, 32, 16, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(fullFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 32, 24, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(goldFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 17, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Ouch!");
                }
        )));
        buttons.add(addDrawableChild(silverFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 28, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Aïe!");
                }
        )));
        buttons.add(addDrawableChild(bronzeFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 39, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(goldFromMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 17, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Ouch!");
                }
        )));
        buttons.add(addDrawableChild(silverMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 28, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Aïe!");
                }
        )));
        buttons.add(addDrawableChild(bronzeMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 39, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(fullFromOtherToMainButton = new DynamicButton(
                this.x + 161, this.y + 17, 8, 32, 16, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(fullFromMainToOtherButton = new DynamicButton(
                this.x + 178, this.y + 17, 8, 32, 24, 0,
                Text.literal("TEST"),
                button ->  {
                    System.out.println("Sacrebleu!");
                }
        )));
        buttons.add(addDrawableChild(displayPlayersButton = new DynamicButton(
                this.x + 71, this.y + 59, 24, 24, 32, 0,
                Text.literal("TEST"),
                button ->  {
                    displayPlayersButton.active = false;
                    displayAccountsButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(displayAccountsButton = new DynamicButton(
                this.x + 38, this.y + 59, 24, 24, 56, 0,
                Text.literal("TEST"),
                button ->  {
                    displayAccountsButton.active = false;
                    displayPlayersButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(displayInfoButton = new DynamicButton(
                this.x + 166, this.y + 59, 24, 24, 80, 0,
                Text.literal("TEST"),
                button ->  {
                    displayInfoButton.active = false;
                    displayTransfertButton.active = false;
                    displaySettingsButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(displayTransfertButton = new DynamicButton(
                this.x + 166, this.y + 59, 24, 24, 104, 0,
                Text.literal("TEST"),
                button ->  {
                    displayTransfertButton.active = false;
                    displayInfoButton.active = false;
                    displaySettingsButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(displaySettingsButton = new DynamicButton(
                this.x + 199, this.y + 59, 24, 24, 128, 0,
                Text.literal("TEST"),
                button ->  {
                    displaySettingsButton.active = false;
                    displayTransfertButton.active = true;
                    displayInfoButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(addAccountButton = new DynamicButton(
                this.x + 5, this.y + 76, 9, 9, 152, 0,
                Text.literal("TEST"),
                button ->  {
                    displaySettingsButton.active = false;
                    displayTransfertButton.active = true;
                    displayInfoButton.active = true;
                }
        )));



        // buttons init
        displayAccountsButton.active = false;
        displayInfoButton.active = false;
        displayTransfertButton.active = false;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateTransferFactor();
        manageButtons(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        drawBalances(context, mouseX, mouseY);
        drawAdditionalTexture(context);
    }

    private void drawBalances(DrawContext context, int mouseX, int mouseY) {
        // Pouch balance: always displayed
        drawBalance(context, 5, mouseX, mouseY, Text.translatable("numismatic_utils.banker.pouch"), "numismatic_utils.tooltip.current_pouch_balance");

        // Personal account balance: always displayed
        drawBalance(context, 96, mouseX, mouseY, Text.translatable("numismatic_utils.banker.main_account"), "numismatic_utils.tooltip.current_main_account_balance");

        // Shared account balance: only displayed if the player has selected a shared account
        if (!displayAccountsButton.active /* && the account selected is not the player's main account */) {
            drawBalance(context, 187, mouseX, mouseY, Text.literal("Cocon d'amour"), "numismatic_utils.tooltip.current_shared_account_balance");
        }
    }

    private void drawBalance(DrawContext context, int horizontalOffset, int mouseX, int mouseY, Text Name, String tooltipTranslationKey) {
        int textX = min(
            this.x + horizontalOffset + (63 - textRenderer.getWidth(Name)) / 2, // Centered
            this.x + this.backgroundWidth - 3 - textRenderer.getWidth(Name)               // Right aligned if text goes out
        );
        context.drawText(this.textRenderer, Name, textX, this.y + 5, 0x17202A, false);
        NumismaticDraw.renderBalanceVertical(
            context,
            this.textRenderer,
            this.x + horizontalOffset,
            this.y + 14,
            ModComponents.CURRENCY.get(client.player).getValue(),
            0xFFFFFF,
            true,
                -6,
            false
        );

        if (mouseX >= this.x + horizontalOffset && mouseX <= this.x + horizontalOffset + 63 && mouseY >= this.y + 14 && mouseY <= this.y + 50) {
            context.drawTooltip(this.textRenderer, Text.translatable(tooltipTranslationKey), mouseX, mouseY);
        }
    }

    private void drawAdditionalTexture(DrawContext context) {

    }

    private void manageButtons(DrawContext context, int mouseX, int mouseY, float delta) {
        goldFromMainToPouchButton.visible = !HandledScreen.hasAltDown();
        silverFromMainToPouchButton.visible = !HandledScreen.hasAltDown();
        bronzeFromMainToPouchButton.visible = !HandledScreen.hasAltDown();
        goldFromPouchToMainButton.visible = !HandledScreen.hasAltDown();
        silverFromPouchToMainButton.visible = !HandledScreen.hasAltDown();
        bronzeFromPouchToMainButton.visible = !HandledScreen.hasAltDown();
        fullFromMainToPouchButton.visible = HandledScreen.hasAltDown();
        fullFromPouchToMainButton.visible = HandledScreen.hasAltDown();
        goldFromOtherToMainButton.visible = !HandledScreen.hasAltDown();
        silverFromOtherToMainButton.visible = !HandledScreen.hasAltDown();
        bronzeFromOtherToMainButton.visible = !HandledScreen.hasAltDown();
        goldFromMainToOtherButton.visible = !HandledScreen.hasAltDown();
        silverMainToOtherButton.visible = !HandledScreen.hasAltDown();
        bronzeMainToOtherButton.visible = !HandledScreen.hasAltDown();
        fullFromOtherToMainButton.visible = HandledScreen.hasAltDown();
        fullFromMainToOtherButton.visible = HandledScreen.hasAltDown();

        displayInfoButton.visible = !displayAccountsButton.active;
        displayTransfertButton.visible = !displayPlayersButton.active;
        addAccountButton.visible = !displayAccountsButton.active;
    }

    private void updateTransferFactor() {
        if (HandledScreen.hasShiftDown()) {
            if (HandledScreen.hasControlDown()) {
                transferFactor = 1000;
            } else {
                transferFactor = 100;
            }
        } else {
            if (HandledScreen.hasControlDown()) {
                transferFactor = 10;
            } else {
                transferFactor = 1;
            }
        }
    }
}
