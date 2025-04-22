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
    int transfertFactor;

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
    private DynamicButton homeButton;
    private DynamicButton displayPlayersButton;
    private DynamicButton displayAccountsButton;
    private DynamicButton displayInfoButton;
    private DynamicButton displayTransfertButton;
    private DynamicButton displaySettingsButton;
    private DynamicButton addAccountButton;

    ArrayList<DynamicButton> fromMainToPouchButtons;
    ArrayList<DynamicButton> fromPouchToMainButtons;
    ArrayList<DynamicButton> fromOtherToMainButtons;
    ArrayList<DynamicButton> fromMainToOtherButtons;
    ArrayList<DynamicButton> smallTransfertButtons;
    ArrayList<DynamicButton> fullTransfertButtons;
    ArrayList<DynamicButton> bronzeAndSilverTransfertButtons;

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
                transferMoneyFromPouchToMain(10000L);
            }
        )));
        buttons.add(addDrawableChild(silverFromMainToPouchButton = new DynamicButton(
            this.x + 70, this.y + 28, 8, 10, 0, 0,
            Text.literal("TEST"),
            button ->  {
                transferMoneyFromPouchToMain(100L);
            }
        )));
        buttons.add(addDrawableChild(bronzeFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 39, 8, 10, 0, 0,
                Text.literal("TEST"),
                button ->  {
                    transferMoneyFromPouchToMain(1L);
                }
        )));
        buttons.add(addDrawableChild(goldFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    transferMoneyFromPouchToMain(-10000L);
                }
        )));
        buttons.add(addDrawableChild(silverFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 28, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    transferMoneyFromPouchToMain(-100L);
                }
        )));
        buttons.add(addDrawableChild(bronzeFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 39, 8, 10, 8, 0,
                Text.literal("TEST"),
                button ->  {
                    transferMoneyFromPouchToMain(-1L);
                }
        )));
        buttons.add(addDrawableChild(fullFromMainToPouchButton = new DynamicButton(
                this.x + 70, this.y + 17, 8, 32, 16, 0,
                Text.literal("TEST"),
                button ->  {
                    transferMoneyFromPouchToMain(Long.MAX_VALUE);
                }
        )));
        buttons.add(addDrawableChild(fullFromPouchToMainButton = new DynamicButton(
                this.x + 87, this.y + 17, 8, 32, 24, 0,
                Text.literal("TEST"),
                button ->  {
                    transferMoneyFromPouchToMain(-Long.MAX_VALUE);
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
        buttons.add(addDrawableChild(homeButton = new DynamicButton(
                this.x + 12, this.y + 59, 24, 24, 152, 0,
                Text.literal("TEST"),
                button ->  {
                    homeButton.active = false;
                    displayAccountsButton.active = true;
                    displayPlayersButton.active = true;

                    fromMainToOtherButtons.forEach((mainToOtherButton) -> {
                        mainToOtherButton.visible = false;
                    });
                    fromOtherToMainButtons.forEach((otherToMainButton) -> {
                        otherToMainButton.visible = false;
                    });
                }
        )));
        buttons.add(addDrawableChild(displayAccountsButton = new DynamicButton(
                this.x + 45, this.y + 59, 24, 24, 56, 0,
                Text.literal("TEST"),
                button ->  {
                    displayAccountsButton.active = false;
                    homeButton.active = true;
                    displayPlayersButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(displayPlayersButton = new DynamicButton(
                this.x + 79, this.y + 59, 24, 24, 32, 0,
                Text.literal("TEST"),
                button ->  {
                    displayPlayersButton.active = false;
                    homeButton.active = true;
                    displayAccountsButton.active = true;

                    fromMainToOtherButtons.forEach((mainToOtherButton) -> {
                        mainToOtherButton.visible = false;
                    });
                    fromOtherToMainButtons.forEach((otherToMainButton) -> {
                        otherToMainButton.visible = false;
                    });
                }
        )));
        buttons.add(addDrawableChild(displayInfoButton = new DynamicButton(
                this.x + 171, this.y + 59, 24, 24, 80, 0,
                Text.literal("TEST"),
                button ->  {
                    displayInfoButton.active = false;
                    displayTransfertButton.active = false;
                    displaySettingsButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(displayTransfertButton = new DynamicButton(
                this.x + 171, this.y + 59, 24, 24, 104, 0,
                Text.literal("TEST"),
                button ->  {
                    displayTransfertButton.active = false;
                    displayInfoButton.active = false;
                    displaySettingsButton.active = true;
                }
        )));
        buttons.add(addDrawableChild(displaySettingsButton = new DynamicButton(
                this.x + 204, this.y + 59, 24, 24, 128, 0,
                Text.literal("TEST"),
                button ->  {
                    displaySettingsButton.active = false;
                    displayTransfertButton.active = true;
                    displayInfoButton.active = true;
                }
        )));
        /*buttons.add(addDrawableChild(addAccountButton = new DynamicButton(
                this.x + 5, this.y + 76, 9, 9, 176, 0,
                Text.literal("TEST"),
                button ->  {
                    displaySettingsButton.active = false;
                    displayTransfertButton.active = true;
                    displayInfoButton.active = true;
                }
        )));*/


        fromMainToPouchButtons = new ArrayList<DynamicButton>() {{
            add(goldFromMainToPouchButton);
            add(silverFromMainToPouchButton);
            add(bronzeFromMainToPouchButton);
            add(fullFromMainToPouchButton);
        }};
        fromPouchToMainButtons = new ArrayList<DynamicButton>() {{
            add(goldFromPouchToMainButton);
            add(silverFromPouchToMainButton);
            add(bronzeFromPouchToMainButton);
            add(fullFromPouchToMainButton);
        }};
        fromOtherToMainButtons = new ArrayList<DynamicButton>() {{
            add(goldFromOtherToMainButton);
            add(silverFromOtherToMainButton);
            add(bronzeFromOtherToMainButton);
            add(fullFromOtherToMainButton);
        }};
        fromMainToOtherButtons = new ArrayList<DynamicButton>() {{
            add(goldFromMainToOtherButton);
            add(silverMainToOtherButton);
            add(bronzeMainToOtherButton);
            add(fullFromMainToOtherButton);
        }};
        smallTransfertButtons = new ArrayList<DynamicButton>() {{
            add(goldFromMainToPouchButton);
            add(silverFromMainToPouchButton);
            add(bronzeFromMainToPouchButton);
            add(goldFromPouchToMainButton);
            add(silverFromPouchToMainButton);
            add(bronzeFromPouchToMainButton);
            add(goldFromOtherToMainButton);
            add(silverFromOtherToMainButton);
            add(bronzeFromOtherToMainButton);
            add(goldFromMainToOtherButton);
            add(silverMainToOtherButton);
            add(bronzeMainToOtherButton);
        }};
        fullTransfertButtons = new ArrayList<DynamicButton>() {{
            add(fullFromMainToPouchButton);
            add(fullFromPouchToMainButton);
            add(fullFromOtherToMainButton);
            add(fullFromMainToOtherButton);
        }};
        bronzeAndSilverTransfertButtons = new ArrayList<DynamicButton>() {{
            add(silverFromMainToPouchButton);
            add(bronzeFromMainToPouchButton);
            add(silverFromPouchToMainButton);
            add(bronzeFromPouchToMainButton);
            add(silverFromOtherToMainButton);
            add(bronzeFromOtherToMainButton);
            add(silverMainToOtherButton);
            add(bronzeMainToOtherButton);
        }};

        // buttons init
        homeButton.active = false;
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
        drawBalance(context, 5, mouseX, mouseY, Text.translatable("numismatic_utils.banker.pouch"), "numismatic_utils.tooltip.current_pouch_balance", ModComponents.CURRENCY.get(client.player).getValue());

        // Personal account balance: always displayed
        drawBalance(context, 96, mouseX, mouseY, Text.translatable("numismatic_utils.banker.main_account"), "numismatic_utils.tooltip.current_main_account_balance", ModComponents.CURRENCY.get(client.player).getValue());

        // Shared account balance: only displayed if the player has selected a shared account
        if (!displayAccountsButton.active /* && the account selected is not the player's main account */) {
            drawBalance(context, 187, mouseX, mouseY, Text.literal("Cocon d'amour"), "numismatic_utils.tooltip.current_shared_account_balance", ModComponents.CURRENCY.get(client.player).getValue());
        }
    }

    private void drawBalance(DrawContext context, int horizontalOffset, int mouseX, int mouseY, Text Name, String tooltipTranslationKey, long value) {
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
            value,
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
        manageButtonsVisibility();
        manageButtonsState();
    }

    private void updateTransferFactor() {
        if (HandledScreen.hasShiftDown()) {
            if (HandledScreen.hasControlDown()) {
                transfertFactor = 1000;
            } else {
                transfertFactor = 100;
            }
        } else {
            if (HandledScreen.hasControlDown()) {
                transfertFactor = 10;
            } else {
                transfertFactor = 1;
            }
        }
    }

    private void manageButtonsVisibility() {
        /* ----- Transferts buttons ----- */
        // From main to pouch
        goldFromMainToPouchButton.visible = !HandledScreen.hasAltDown();
        silverFromMainToPouchButton.visible = !HandledScreen.hasAltDown();
        bronzeFromMainToPouchButton.visible = !HandledScreen.hasAltDown();
        fullFromMainToPouchButton.visible = HandledScreen.hasAltDown();

        // From pouch to main
        goldFromPouchToMainButton.visible = !HandledScreen.hasAltDown();
        silverFromPouchToMainButton.visible = !HandledScreen.hasAltDown();
        bronzeFromPouchToMainButton.visible = !HandledScreen.hasAltDown();
        fullFromPouchToMainButton.visible = HandledScreen.hasAltDown();

        // From other to main
        goldFromOtherToMainButton.visible = !HandledScreen.hasAltDown() && !displayAccountsButton.active; // TODO: Add logic to check whether a relevant account is selected and if the player has rights on it
        silverFromOtherToMainButton.visible = !HandledScreen.hasAltDown() && !displayAccountsButton.active;
        bronzeFromOtherToMainButton.visible = !HandledScreen.hasAltDown() && !displayAccountsButton.active;
        fullFromOtherToMainButton.visible = HandledScreen.hasAltDown() && !displayAccountsButton.active;

        // From main to other
        goldFromMainToOtherButton.visible = !HandledScreen.hasAltDown() && !displayAccountsButton.active;
        silverMainToOtherButton.visible = !HandledScreen.hasAltDown() && !displayAccountsButton.active;
        bronzeMainToOtherButton.visible = !HandledScreen.hasAltDown() && !displayAccountsButton.active;
        fullFromMainToOtherButton.visible = HandledScreen.hasAltDown() && !displayAccountsButton.active;

        /* ----- Right side categories ----- */
        displayInfoButton.visible = !homeButton.active || !displayAccountsButton.active;
        displayTransfertButton.visible = !displayPlayersButton.active;
    }

    private void manageButtonsState() {
        fromMainToPouchButtons.forEach((button) -> {
            button.active = true; // TODO: Check based on the account's balance
        });
        fromPouchToMainButtons.forEach((button) -> {
            button.active = ModComponents.CURRENCY.get(client.player).getValue() > 0; // TODO: Change the way to get the money by a unique way accross the method
        });
        fromOtherToMainButtons.forEach((button) -> {
            button.active = true; // TODO: Check based on the account's balance
        });
        fromMainToOtherButtons.forEach((button) -> {
            button.active = true; // TODO: Check based on the account's balance
        });
        if (transfertFactor >= 100) {
            bronzeAndSilverTransfertButtons.forEach((button) -> {
                button.active = false;
            });
        }
    }

    private void transferMoneyFromPouchToMain(long value) {
        value *= transfertFactor;
        if (value < 0) {
            value = max(value, -ModComponents.CURRENCY.get(client.player).getValue());
        } else {
            value = min(value, 250000L);   // Consider the personal account max balance instead
        }
        ModComponents.CURRENCY.get(client.player).silentModify(value);
    }
}
