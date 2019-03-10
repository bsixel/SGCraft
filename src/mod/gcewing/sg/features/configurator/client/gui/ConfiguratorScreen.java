package gcewing.sg.features.configurator.client.gui;

import com.google.common.eventbus.Subscribe;
import gcewing.sg.features.configurator.network.ConfiguratorNetworkHandler;
import gcewing.sg.tileentity.SGBaseTE;
import gcewing.sg.util.GateUtil;
import gcewing.sg.util.SGAddressing;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.BasicScreen;
import net.malisis.core.client.gui.component.container.BasicForm;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.decoration.UISeparator;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UICheckBox;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.malisis.core.client.gui.component.interaction.button.builder.UIButtonBuilder;
import net.malisis.core.client.gui.event.component.StateChangeEvent;
import net.malisis.core.renderer.font.FontOptions;
import net.malisis.core.util.FontColors;
import net.malisis.core.util.MathUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ConfiguratorScreen extends BasicScreen {
    private int lastUpdate = 0;
    private boolean unlockMouse = true;
    private boolean isAdmin;
    private BasicForm form, numericOptionsArea, checkboxOptionsArea;
    private UILabel gateAddressLabel;
    private UICheckBox oneWayTravelCheckbox, irisUpgradeCheckbox, chevronUpgradeCheckbox, gateTypeCheckbox, reverseWormholeKillsCheckbox, acceptIncomingConnectionsCheckbox, closeFromEitherEndCheckbox, preserveInventoryCheckbox, noPowerRequiredCheckbox, chevronsLockOnDialCheckbox, returnIrisToPreviousStateCheckbox;
    private UITextField secondsToStayOpen, gateRotationSpeed, energyBufferSize, energyPerNaquadah, gateOpeningsPerNaquadah, distanceMultiplier, dimensionalMultiplier;
    private BlockPos location;
    private World world;
    private EntityPlayer player;

    public ConfiguratorScreen(EntityPlayer player, World worldIn,  boolean isAdmin) {
        this.player = player;
        this.isAdmin = isAdmin;
        this.world = worldIn;
        this.location = new BlockPos(player.posX, player.posY, player.posZ);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void construct() {
        this.guiscreenBackground = false;
        Keyboard.enableRepeatEvents(true);

        TileEntity localGateTE = GateUtil.locateLocalGate(this.world, this.location, 6, true);

        if (!(localGateTE instanceof SGBaseTE)) {
            // Desync between server and client.  Client doesn't have TE data yet.
            return;
        }

        SGBaseTE localGate = (SGBaseTE) localGateTE;

        // Master Panel
        this.form = new BasicForm(this, 500, 225, "");
        this.form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        this.form.setMovable(true);
        this.form.setClosable(true);
        this.form.setBorder(FontColors.WHITE, 1, 185);
        this.form.setBackgroundAlpha(215);
        this.form.setBottomPadding(3);
        this.form.setRightPadding(3);
        this.form.setTopPadding(20);
        this.form.setLeftPadding(3);

        final UILabel titleLabel = new UILabel(this, "Configurator");
        titleLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        titleLabel.setPosition(0, -15, Anchor.CENTER | Anchor.TOP);

        // ****************************************************************************************************************************

        this.numericOptionsArea = new BasicForm(this, 245, 185, "");
        this.numericOptionsArea.setPosition(0, 0, Anchor.LEFT | Anchor.MIDDLE);
        this.numericOptionsArea.setMovable(false);
        this.numericOptionsArea.setClosable(false);
        this.numericOptionsArea.setBorder(FontColors.WHITE, 1, 185);
        this.numericOptionsArea.setBackgroundAlpha(215);
        this.numericOptionsArea.setBottomPadding(3);
        this.numericOptionsArea.setRightPadding(3);
        this.numericOptionsArea.setTopPadding(3);
        this.numericOptionsArea.setLeftPadding(3);

        int padding = 15;

        final UILabel numericValuesLabel = new UILabel(this, "Numeric Values");
        numericValuesLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        numericValuesLabel.setPosition(0, 0, Anchor.CENTER | Anchor.TOP);

        final UISeparator valuesSeparator = new UISeparator(this);
        valuesSeparator.setSize(this.numericOptionsArea.getWidth() - 15, 1);
        valuesSeparator.setPosition(0, 10, Anchor.TOP | Anchor.CENTER);

        final UILabel secondsToStayOpenLabel = new UILabel(this, "Seconds to Stay Open:");
        secondsToStayOpenLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        secondsToStayOpenLabel.setPosition(10, numericValuesLabel.getY() + padding + 5, Anchor.LEFT | Anchor.TOP);

        this.secondsToStayOpen = new UITextField(this, "", false);
        this.secondsToStayOpen.setSize(45, 0);
        this.secondsToStayOpen.setEditable(this.isAdmin);
        this.secondsToStayOpen.setPosition(-10, secondsToStayOpenLabel.getY(), Anchor.RIGHT | Anchor.TOP);
        this.secondsToStayOpen.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());
        this.secondsToStayOpen.setFilter(s -> {
            try {
                final int value = Integer.parseInt(s.replaceAll("[^\\d]", ""));
                return String.valueOf(MathUtil.squashi(value, 0, 50000));
            } catch (NumberFormatException e) {
                return "";
            }
        });
        this.secondsToStayOpen.register(this);

        final UILabel gateRotationSpeedLabel = new UILabel(this, "Gate Rotation Speed:");
        gateRotationSpeedLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        gateRotationSpeedLabel.setPosition(10, secondsToStayOpenLabel.getY() + padding, Anchor.LEFT | Anchor.TOP);

        this.gateRotationSpeed = new UITextField(this, "", false);
        this.gateRotationSpeed.setSize(45, 0);
        this.gateRotationSpeed.setEditable(this.isAdmin);
        this.gateRotationSpeed.setPosition(-10, gateRotationSpeedLabel.getY(), Anchor.RIGHT | Anchor.TOP);
        this.gateRotationSpeed.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());
        this.gateRotationSpeed.setFilter(s -> {
            try {
                final double value = Double.parseDouble(s.replaceAll("[^\\d.]", ""));
                return String.valueOf(MathUtil.squashd(value, 1.0, 10.0));
            } catch (NumberFormatException e) {
                return "";
            }
        });
        this.gateRotationSpeed.register(this);

        final UILabel energyBufferMaxSizeLabel = new UILabel(this, "Energy Buffer Size:");
        energyBufferMaxSizeLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        energyBufferMaxSizeLabel.setPosition(10, gateRotationSpeedLabel.getY() + padding, Anchor.LEFT | Anchor.TOP);

        this.energyBufferSize = new UITextField(this, "", false);
        this.energyBufferSize.setSize(45, 0);
        this.energyBufferSize.setEditable(this.isAdmin);
        this.energyBufferSize.setPosition(-10, energyBufferMaxSizeLabel.getY(), Anchor.RIGHT | Anchor.TOP);
        this.energyBufferSize.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());
        this.energyBufferSize.setFilter(s -> {
            try {
                final double value = Double.parseDouble(s.replaceAll("[^\\d.]", ""));
                return String.valueOf(MathUtil.squashd(value, 0.0, 5000000.0));
            } catch (NumberFormatException e) {
                return "";
            }
        });
        this.energyBufferSize.register(this);

        final UILabel energyPerItemLabel = new UILabel(this, "Energy per Naquadah:");
        energyPerItemLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        energyPerItemLabel.setPosition(10, energyBufferMaxSizeLabel.getY() + padding, Anchor.LEFT | Anchor.TOP);

        this.energyPerNaquadah = new UITextField(this, "", false);
        this.energyPerNaquadah.setSize(45, 0);
        this.energyPerNaquadah.setEditable(this.isAdmin);
        this.energyPerNaquadah.setPosition(-10, energyPerItemLabel.getY(), Anchor.RIGHT | Anchor.TOP);
        this.energyPerNaquadah.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());
        this.energyPerNaquadah.setFilter(s -> {
            try {
                final double value = Double.parseDouble(s.replaceAll("[^\\d.]", ""));
                return String.valueOf(MathUtil.squashd(value, 0.0, 250000.0));
            } catch (NumberFormatException e) {
                return "";
            }
        });
        this.energyPerNaquadah.register(this);

        final UILabel energyPerOpeningLabel = new UILabel(this, "Openings per Naquadah:");
        energyPerOpeningLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        energyPerOpeningLabel.setPosition(10, energyPerItemLabel.getY() + padding, Anchor.LEFT | Anchor.TOP);

        this.gateOpeningsPerNaquadah = new UITextField(this, "", false);
        this.gateOpeningsPerNaquadah.setSize(45, 0);
        this.gateOpeningsPerNaquadah.setEditable(this.isAdmin);
        this.gateOpeningsPerNaquadah.setPosition(-10, energyPerOpeningLabel.getY(), Anchor.RIGHT | Anchor.TOP);
        this.gateOpeningsPerNaquadah.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());
        this.gateOpeningsPerNaquadah.setFilter(s -> {
            try {
                final int value = Integer.parseInt(s.replaceAll("[^\\d]", ""));
                return String.valueOf(MathUtil.squashi(value, 0, 50000));
            } catch (NumberFormatException e) {
                return "";
            }
        });
        this.gateOpeningsPerNaquadah.register(this);

        final UILabel distanceFactorMultiplierLabel = new UILabel(this, "Distance Factor Multiplier:");
        distanceFactorMultiplierLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        distanceFactorMultiplierLabel.setPosition(10, energyPerOpeningLabel.getY() + padding, Anchor.LEFT | Anchor.TOP);

        this.distanceMultiplier = new UITextField(this, "", false);
        this.distanceMultiplier.setSize(45, 0);
        this.distanceMultiplier.setEditable(this.isAdmin);
        this.distanceMultiplier.setPosition(-10, distanceFactorMultiplierLabel.getY(), Anchor.RIGHT | Anchor.TOP);
        this.distanceMultiplier.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());
        this.distanceMultiplier.setFilter(s -> {
            try {
                final double value = Double.parseDouble(s.replaceAll("[^\\d.]", ""));
                return String.valueOf(MathUtil.squashd(value, 0.0, 1000.0));
            } catch (NumberFormatException e) {
                return "";
            }
        });
        this.distanceMultiplier.register(this);

        final UILabel interDimensionalMultiplierLabel = new UILabel(this, "Inter-Dimensional Multiplier:");
        interDimensionalMultiplierLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        interDimensionalMultiplierLabel.setPosition(10, distanceFactorMultiplierLabel.getY() + padding, Anchor.LEFT | Anchor.TOP);

        this.dimensionalMultiplier = new UITextField(this, "", false);
        this.dimensionalMultiplier.setSize(45, 0);
        this.dimensionalMultiplier.setEditable(this.isAdmin);
        this.dimensionalMultiplier.setPosition(-10, interDimensionalMultiplierLabel.getY(), Anchor.RIGHT | Anchor.TOP);
        this.dimensionalMultiplier.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());
        this.dimensionalMultiplier.setFilter(s -> {
            try {
                final double value = Double.parseDouble(s.replaceAll("[^\\d.]", ""));
                return String.valueOf(MathUtil.squashd(value, 0.0, 1000.0));
            } catch (NumberFormatException e) {
                return "";
            }
        });
        this.dimensionalMultiplier.register(this);

        this.numericOptionsArea.add(numericValuesLabel, valuesSeparator, secondsToStayOpenLabel, gateRotationSpeedLabel, energyBufferMaxSizeLabel, energyPerItemLabel, energyPerOpeningLabel, distanceFactorMultiplierLabel, interDimensionalMultiplierLabel);
        this.numericOptionsArea.add(this.secondsToStayOpen, this.gateRotationSpeed, this.energyBufferSize, this.energyPerNaquadah, this.gateOpeningsPerNaquadah, this.distanceMultiplier, this.dimensionalMultiplier);

        // ****************************************************************************************************************************

        this.checkboxOptionsArea = new BasicForm(this, 245, 185, "");
        this.checkboxOptionsArea.setPosition(0, 0, Anchor.RIGHT | Anchor.MIDDLE);
        this.checkboxOptionsArea.setMovable(false);
        this.checkboxOptionsArea.setClosable(false);
        this.checkboxOptionsArea.setBorder(FontColors.WHITE, 1, 185);
        this.checkboxOptionsArea.setBackgroundAlpha(215);
        this.checkboxOptionsArea.setBottomPadding(3);
        this.checkboxOptionsArea.setRightPadding(3);
        this.checkboxOptionsArea.setTopPadding(3);
        this.checkboxOptionsArea.setLeftPadding(3);

        int checkboxIndentPadding = 25;
        padding = 12;

        final UILabel booleanValuesLabel = new UILabel(this, "Boolean Values");
        booleanValuesLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        booleanValuesLabel.setPosition(0, 0, Anchor.CENTER | Anchor.TOP);

        final UISeparator checkboxSeparator = new UISeparator(this);
        checkboxSeparator.setSize(this.checkboxOptionsArea.getWidth() - 15, 1);
        checkboxSeparator.setPosition(0, 10, Anchor.TOP | Anchor.CENTER);

        this.oneWayTravelCheckbox = new UICheckBox(this);
        this.oneWayTravelCheckbox.setText(TextFormatting.WHITE + "One Way Travel Only");
        this.oneWayTravelCheckbox.setPosition(checkboxIndentPadding, 15, Anchor.LEFT | Anchor.TOP);
        this.oneWayTravelCheckbox.setName("checkbox.onewaytravel");
        this.oneWayTravelCheckbox.register(this);

        this.irisUpgradeCheckbox = new UICheckBox(this);
        this.irisUpgradeCheckbox.setText(TextFormatting.WHITE + "Iris Upgrade");
        this.irisUpgradeCheckbox.setPosition(checkboxIndentPadding, this.oneWayTravelCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.irisUpgradeCheckbox.setName("checkbox.irisupgrade");
        this.irisUpgradeCheckbox.register(this);

        this.chevronUpgradeCheckbox = new UICheckBox(this);
        this.chevronUpgradeCheckbox.setText(TextFormatting.WHITE + "Chevron Upgrade");
        this.chevronUpgradeCheckbox.setPosition(checkboxIndentPadding, this.irisUpgradeCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.chevronUpgradeCheckbox.setName("checkbox.chevronupgrade");
        this.chevronUpgradeCheckbox.register(this);

        this.gateTypeCheckbox = new UICheckBox(this);
        this.gateTypeCheckbox.setText(TextFormatting.WHITE + "Pegasus Gate Type");
        this.gateTypeCheckbox.setPosition(checkboxIndentPadding, this.chevronUpgradeCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.gateTypeCheckbox.setName("checkbox.gateType");
        this.gateTypeCheckbox.register(this);

        this.reverseWormholeKillsCheckbox = new UICheckBox(this);
        this.reverseWormholeKillsCheckbox.setText(TextFormatting.WHITE + "Reverse Wormhole Kills");
        this.reverseWormholeKillsCheckbox.setPosition(checkboxIndentPadding, this.gateTypeCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.reverseWormholeKillsCheckbox.setName("checkbox.reversekills");
        this.reverseWormholeKillsCheckbox.register(this);

        this.acceptIncomingConnectionsCheckbox = new UICheckBox(this);
        this.acceptIncomingConnectionsCheckbox.setText(TextFormatting.WHITE + "Accept Incoming Connections");
        this.acceptIncomingConnectionsCheckbox.setPosition(checkboxIndentPadding, this.reverseWormholeKillsCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.acceptIncomingConnectionsCheckbox.setEnabled(true);
        this.acceptIncomingConnectionsCheckbox.setName("checkbox.acceptincomingconnections");
        this.acceptIncomingConnectionsCheckbox.register(this);

        this.closeFromEitherEndCheckbox = new UICheckBox(this);
        this.closeFromEitherEndCheckbox.setText(TextFormatting.WHITE + "Close from Either End");
        this.closeFromEitherEndCheckbox.setPosition(checkboxIndentPadding, this.acceptIncomingConnectionsCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.closeFromEitherEndCheckbox.setName("checkbox.canbedialedto");
        this.closeFromEitherEndCheckbox.register(this);

        this.preserveInventoryCheckbox = new UICheckBox(this);
        this.preserveInventoryCheckbox.setText(TextFormatting.WHITE + "Preserve Inventory on Iris Death");
        this.preserveInventoryCheckbox.setPosition(checkboxIndentPadding, this.closeFromEitherEndCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.preserveInventoryCheckbox.setName("checkbox.canbedialedto");
        this.preserveInventoryCheckbox.register(this);

        this.noPowerRequiredCheckbox = new UICheckBox(this);
        this.noPowerRequiredCheckbox.setText(TextFormatting.WHITE + "No Input Power Required");
        this.noPowerRequiredCheckbox.setPosition(checkboxIndentPadding, this.preserveInventoryCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.noPowerRequiredCheckbox.setName("checkbox.nopowerrequired");
        this.noPowerRequiredCheckbox.register(this);

        this.chevronsLockOnDialCheckbox = new UICheckBox(this);
        this.chevronsLockOnDialCheckbox.setText(TextFormatting.WHITE + "Chevrons lock when dialed");
        this.chevronsLockOnDialCheckbox.setTooltip("Chevrons lock when dialed from PDD or DHD, no ring rotation dialing sequence.");
        this.chevronsLockOnDialCheckbox.setPosition(checkboxIndentPadding, this.noPowerRequiredCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.chevronsLockOnDialCheckbox.setEnabled(true);
        this.chevronsLockOnDialCheckbox.setName("checkbox.chevronlockondial");
        this.chevronsLockOnDialCheckbox.register(this);

        this.returnIrisToPreviousStateCheckbox = new UICheckBox(this);
        this.returnIrisToPreviousStateCheckbox.setText(TextFormatting.WHITE + "Return to Previous Iris State");
        this.returnIrisToPreviousStateCheckbox.setTooltip("This will return the iris state to what it was prior to dialing.");
        this.returnIrisToPreviousStateCheckbox.setPosition(checkboxIndentPadding, this.chevronsLockOnDialCheckbox.getY() + padding, Anchor.LEFT | Anchor.TOP);
        this.returnIrisToPreviousStateCheckbox.setEnabled(true);
        this.returnIrisToPreviousStateCheckbox.setName("checkbox.returntopreviousirisstate");
        this.returnIrisToPreviousStateCheckbox.register(this);

        this.checkboxOptionsArea.add(booleanValuesLabel, checkboxSeparator, this.oneWayTravelCheckbox, this.irisUpgradeCheckbox, this.chevronUpgradeCheckbox, this.gateTypeCheckbox);
        this.checkboxOptionsArea.add(this.reverseWormholeKillsCheckbox, this.acceptIncomingConnectionsCheckbox, this.closeFromEitherEndCheckbox, this.preserveInventoryCheckbox, this.noPowerRequiredCheckbox);
        this.checkboxOptionsArea.add(this.chevronsLockOnDialCheckbox, this.returnIrisToPreviousStateCheckbox);

        // Load Defaults button
        final UIButton buttonDefaults = new UIButtonBuilder(this)
            .width(40)
            .anchor(Anchor.BOTTOM | Anchor.LEFT)
            .text("Load Defaults")
            .onClick(() -> {
                secondsToStayOpen.setText(String.valueOf(SGBaseTE.cfg.getInteger("stargate", "secondsToStayOpen", 500)));
                gateRotationSpeed.setText(String.valueOf(2.0)); // Isn't contained in base config file
                energyBufferSize.setText(String.valueOf(SGBaseTE.cfg.getDouble("stargate", "maxEnergyBuffer", 2500.0)));
                energyPerNaquadah.setText(String.valueOf(SGBaseTE.cfg.getDouble("stargate", "energyPerFuelItem", 25000.0)));
                gateOpeningsPerNaquadah.setText(String.valueOf(SGBaseTE.cfg.getInteger("stargate", "gateOpeningsPerFuelItem", 10)));
                distanceMultiplier.setText(String.valueOf(SGBaseTE.cfg.getDouble("stargate", "distanceFactorMultiplier", 1.0)));
                dimensionalMultiplier.setText(String.valueOf(SGBaseTE.cfg.getDouble("stargate", "interDimensionMultiplier", 4.0)));
                oneWayTravelCheckbox.setChecked(SGBaseTE.cfg.getBoolean("stargate", "oneWayTravel", true));
                irisUpgradeCheckbox.setChecked(false);
                chevronUpgradeCheckbox.setChecked(false);
                reverseWormholeKillsCheckbox.setChecked(SGBaseTE.cfg.getBoolean("stargate", "reverseWormholeKills", false));
                acceptIncomingConnectionsCheckbox.setChecked(true);
                closeFromEitherEndCheckbox.setChecked(SGBaseTE.cfg.getBoolean("stargate", "closeFromEitherEnd", true));
                preserveInventoryCheckbox.setChecked(SGBaseTE.cfg.getBoolean("iris", "preserveInventory", false));
                noPowerRequiredCheckbox.setChecked(false);
                chevronsLockOnDialCheckbox.setChecked(false);
                returnIrisToPreviousStateCheckbox.setChecked(false);
            })
            .listener(this)
            .build("button.defaults");

        // Save button
        final UIButton buttonSave = new UIButtonBuilder(this)
            .width(40)
            .anchor(Anchor.BOTTOM | Anchor.RIGHT)
            .position(-40, 0)
            .text("Save")
            .onClick(() -> {
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 1, Integer.valueOf(secondsToStayOpen.getText()), false, 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 2, 0, false, Double.valueOf(gateRotationSpeed.getText()));
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 3, 0, false, Double.valueOf(energyBufferSize.getText()));
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 4, 0, false, Double.valueOf(energyPerNaquadah.getText()));
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 5, Integer.valueOf(gateOpeningsPerNaquadah.getText()), false, 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 6, 0, false, Double.valueOf(distanceMultiplier.getText()));
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 7, 0, false, Double.valueOf(dimensionalMultiplier.getText()));
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 8, 0, oneWayTravelCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 9, 0, irisUpgradeCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 10, 0, chevronUpgradeCheckbox.isChecked(), 0.0);
                if (gateTypeCheckbox.isChecked()) {
                    ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 11, 2, false, 0.0);
                } else {
                    ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 11, 1, false, 0.0);
                }
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 12, 0, reverseWormholeKillsCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 13, 0, acceptIncomingConnectionsCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 14, 0, closeFromEitherEndCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 15, 0, preserveInventoryCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 16, 0, noPowerRequiredCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 17, 0, chevronsLockOnDialCheckbox.isChecked(), 0.0);
                ConfiguratorNetworkHandler.sendConfiguratorInputToServer(localGate, 18, 0, returnIrisToPreviousStateCheckbox.isChecked(), 0.0);
                this.close();
            })
            .listener(this)
            .build("button.save");

        final UILabel addressLabel = new UILabel(this, "Gate Address: ");
        addressLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        addressLabel.setPosition(-40, -3, Anchor.CENTER | Anchor.BOTTOM);

        this.gateAddressLabel = new UILabel(this, "");
        this.gateAddressLabel.setFontOptions(FontOptions.builder().from(FontColors.BLUE_FO).shadow(true).scale(1.1F).build());
        this.gateAddressLabel.setPosition(40, -2, Anchor.CENTER | Anchor.BOTTOM);

        // Close button
        final UIButton buttonClose = new UIButtonBuilder(this)
            .width(40)
            .anchor(Anchor.BOTTOM | Anchor.RIGHT)
            .text("Close")
            .onClick(() -> {
                this.close();
            })
            .listener(this)
            .build("button.close");

        this.form.add(titleLabel, this.numericOptionsArea, this.checkboxOptionsArea, buttonDefaults, buttonSave, addressLabel, this.gateAddressLabel, buttonClose);
        addToScreen(this.form);
        this.refresh();
    }

    @Subscribe
    public void onFocusStateChange(StateChangeEvent.FocusStateChange<UITextField> event) {
        TileEntity localGate = GateUtil.locateLocalGate(this.world, this.location, 6, true);

        if (localGate == null)
            return;
        if  (!(localGate instanceof SGBaseTE))
            return;

        if (secondsToStayOpen.getText().isEmpty()) {
            secondsToStayOpen.setText(String.valueOf(((SGBaseTE) localGate).secondsToStayOpen));
        }
        if (gateRotationSpeed.getText().isEmpty()) {
            gateRotationSpeed.setText(String.valueOf(((SGBaseTE) localGate).ringRotationSpeed));
        }
        if (energyBufferSize.getText().isEmpty()) {
            energyBufferSize.setText(String.valueOf(((SGBaseTE) localGate).maxEnergyBuffer));
        }
        if (energyPerNaquadah.getText().isEmpty()) {
            energyPerNaquadah.setText(String.valueOf(((SGBaseTE) localGate).energyPerFuelItem));
        }
        if (gateOpeningsPerNaquadah.getText().isEmpty()) {
            gateOpeningsPerNaquadah.setText(String.valueOf(((SGBaseTE) localGate).gateOpeningsPerFuelItem));
        }
        if (distanceMultiplier.getText().isEmpty()) {
            distanceMultiplier.setText(String.valueOf(((SGBaseTE) localGate).distanceFactorMultiplier));
        }
        if (dimensionalMultiplier.getText().isEmpty()) {
            dimensionalMultiplier.setText(String.valueOf(((SGBaseTE) localGate).interDimensionMultiplier));
        }
    }

    private void refresh() {
        TileEntity localGate = GateUtil.locateLocalGate(this.world, this.location, 6, true);
        if (localGate == null) {
            return;
        }

        if  (!(localGate instanceof SGBaseTE)) {
            return;
        }

        this.gateAddressLabel.setText(SGAddressing.formatAddress(((SGBaseTE) localGate).homeAddress, "-", "-"));

        // Numeric Values
        this.secondsToStayOpen.setText(String.valueOf(((SGBaseTE) localGate).secondsToStayOpen));
        this.gateRotationSpeed.setText(String.valueOf(((SGBaseTE) localGate).ringRotationSpeed));
        this.energyBufferSize.setText(String.valueOf(((SGBaseTE) localGate).maxEnergyBuffer));
        this.energyPerNaquadah.setText(String.valueOf(((SGBaseTE) localGate).energyPerFuelItem));
        this.gateOpeningsPerNaquadah.setText(String.valueOf(((SGBaseTE) localGate).gateOpeningsPerFuelItem));
        this.distanceMultiplier.setText(String.valueOf(((SGBaseTE) localGate).distanceFactorMultiplier));
        this.dimensionalMultiplier.setText(String.valueOf(((SGBaseTE) localGate).interDimensionMultiplier));

        // Boolean Values
        this.oneWayTravelCheckbox.setChecked(((SGBaseTE) localGate).oneWayTravel);
        this.irisUpgradeCheckbox.setChecked(((SGBaseTE) localGate).hasIrisUpgrade);
        this.chevronUpgradeCheckbox.setChecked(((SGBaseTE) localGate).hasChevronUpgrade);
        if (((SGBaseTE) localGate).gateType == 0 || ((SGBaseTE) localGate).gateType == 1) {
            this.gateTypeCheckbox.setChecked(false);
        } else {
            this.gateTypeCheckbox.setChecked(true);
        }
        this.reverseWormholeKillsCheckbox.setChecked(((SGBaseTE) localGate).reverseWormholeKills);
        this.acceptIncomingConnectionsCheckbox.setChecked(((SGBaseTE) localGate).acceptIncomingConnections);
        this.closeFromEitherEndCheckbox.setChecked(((SGBaseTE) localGate).closeFromEitherEnd);
        this.preserveInventoryCheckbox.setChecked(((SGBaseTE) localGate).preserveInventory);
        this.noPowerRequiredCheckbox.setChecked(((SGBaseTE) localGate).requiresNoPower);
        this.chevronsLockOnDialCheckbox.setChecked(((SGBaseTE) localGate).chevronsLockOnDial);
        this.returnIrisToPreviousStateCheckbox.setChecked(((SGBaseTE) localGate).returnToPreviousIrisState);
    }

    @Override
    public void update(int mouseX, int mouseY, float partialTick) {
        super.update(mouseX, mouseY, partialTick);
        if (this.unlockMouse && this.lastUpdate == 25) {
            Mouse.setGrabbed(false); // Force the mouse to be visible even though Mouse.isGrabbed() is false.  //#BugsUnited.
            this.unlockMouse = false; // Only unlock once per session.
        }

        if (++this.lastUpdate > 30) {
            this.lastUpdate = 0;
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        if (keyCode == Keyboard.KEY_TAB) {
            if (this.secondsToStayOpen.isFocused()) {
                this.gateRotationSpeed.setFocused(true);
                return;
            }
            if (this.gateRotationSpeed.isFocused()) {
                this.energyBufferSize.setFocused(true);
                return;
            }
            if (this.energyBufferSize.isFocused()) {
                this.energyPerNaquadah.setFocused(true);
                return;
            }
            if (this.energyPerNaquadah.isFocused()) {
                this.gateOpeningsPerNaquadah.setFocused(true);
                return;
            }
            if (this.gateOpeningsPerNaquadah.isFocused()) {
                this.distanceMultiplier.setFocused(true);
                return;
            }
            if (this.distanceMultiplier.isFocused()) {
                this.dimensionalMultiplier.setFocused(true);
                return;
            }
            if (this.dimensionalMultiplier.isFocused()) {
                this.secondsToStayOpen.setFocused(true);
                return;
            }
        }
        super.keyTyped(keyChar, keyCode);
        this.lastUpdate = 0; // Reset the timer when key is typed.
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        this.lastUpdate = 0; // Reset the timer when mouse is pressed.
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // Can't stop the game otherwise the Sponge Scheduler also stops.
    }
}