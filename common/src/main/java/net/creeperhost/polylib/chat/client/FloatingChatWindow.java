package net.creeperhost.polylib.chat.client;

import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.ChatMember;
import net.creeperhost.polylib.chat.RichChatMessage;
import net.creeperhost.polylib.client.modulargui.elements.GuiButton;
import net.creeperhost.polylib.client.modulargui.elements.GuiRectangle;
import net.creeperhost.polylib.client.modulargui.elements.GuiScrolling;
import net.creeperhost.polylib.client.modulargui.elements.GuiText;
import net.creeperhost.polylib.client.modulargui.elements.GuiTextField;
import net.creeperhost.polylib.client.modulargui.elements.GuiTexture;
import net.creeperhost.polylib.client.modulargui.elements.GuiWindow;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;

/**
 * The actual chat UI that renders a ChatChannel.
 */
public class FloatingChatWindow extends GuiWindow {

    private final ChatChannel channel;
    
    private final GuiRectangle sidebar;
    private final GuiScrolling messageArea;
    private final GuiTextField inputField;

    public FloatingChatWindow(GuiParent<?> parent, ChatChannel channel) {
        super(parent);
        this.channel = channel;
        
        setTitle(channel.getName());

        // Setup Sidebar
        this.sidebar = new GuiRectangle(getBackground());
        this.sidebar.constrain(GeoParam.RIGHT, Constraint.match(getBackground().get(GeoParam.RIGHT)))
                    .constrain(GeoParam.TOP, Constraint.match(getHeaderBar().get(GeoParam.BOTTOM)))
                    .constrain(GeoParam.BOTTOM, Constraint.match(getBackground().get(GeoParam.BOTTOM)))
                    .constrain(GeoParam.WIDTH, Constraint.literal(50))
                    .fill(0x55000000);

        // Setup Message Area
        this.messageArea = new GuiScrolling(getBackground());
        this.messageArea.constrain(GeoParam.LEFT, Constraint.match(getBackground().get(GeoParam.LEFT)))
                        .constrain(GeoParam.RIGHT, Constraint.match(sidebar.get(GeoParam.LEFT)))
                        .constrain(GeoParam.TOP, Constraint.match(getHeaderBar().get(GeoParam.BOTTOM)))
                        .constrain(GeoParam.BOTTOM, Constraint.relative(getBackground().get(GeoParam.BOTTOM), channel.canReply() ? -20 : 0));

        // Setup Input Field (only if channel supports replies)
        if (channel.canReply()) {
            this.inputField = new GuiTextField(getBackground());
            this.inputField.constrain(GeoParam.LEFT, Constraint.match(getBackground().get(GeoParam.LEFT)))
                           .constrain(GeoParam.RIGHT, Constraint.match(getBackground().get(GeoParam.RIGHT)))
                           .constrain(GeoParam.BOTTOM, Constraint.match(getBackground().get(GeoParam.BOTTOM)))
                           .constrain(GeoParam.HEIGHT, Constraint.literal(20));
                           
            this.inputField.setEnterPressed(() -> {
                String text = this.inputField.getValue();
                if (!text.isEmpty()) {
                    this.channel.submitMessage(text);
                    this.inputField.setValue("");
                }
            });
        } else {
            this.inputField = null;
        }

        // Hook up listeners
        this.channel.addMessageListener(this::onNewMessage);
        
        // Initial render
        refreshMembers();
        refreshMessages();
    }

    private void onNewMessage(RichChatMessage message) {
        refreshMessages();
    }

    private void refreshMembers() {
        new java.util.ArrayList<>(sidebar.getChildren()).forEach(sidebar::removeChild);
        
        GuiText headerText = new GuiText(sidebar);
        headerText.setText(net.minecraft.network.chat.Component.literal("Members"));
        headerText.constrain(GeoParam.LEFT, Constraint.relative(sidebar.get(GeoParam.LEFT), 2))
                .constrain(GeoParam.TOP, Constraint.relative(sidebar.get(GeoParam.TOP), 2));
        
        GuiElement<?> lastElement = headerText;
        
        for (ChatMember member : channel.getMembers()) {
            GuiText nameText = new GuiText(sidebar);
            nameText.setText(member.displayName());
            nameText.constrain(GeoParam.LEFT, Constraint.relative(sidebar.get(GeoParam.LEFT), 2))
                    .constrain(GeoParam.TOP, Constraint.relative(lastElement.get(GeoParam.BOTTOM), 2))
                    .constrain(GeoParam.RIGHT, Constraint.relative(sidebar.get(GeoParam.RIGHT), -2))
                    .setWrap(true).setAlignment(net.creeperhost.polylib.client.modulargui.lib.geometry.Align.MIN).autoHeight();
            
            lastElement = nameText;
        }
    }

    private void refreshMessages() {
        new java.util.ArrayList<>(messageArea.getContentElement().getChildren()).forEach(messageArea.getContentElement()::removeChild);
        
        // Prevent horizontal scroll by constraining width
        messageArea.getContentElement().constrain(GeoParam.WIDTH, Constraint.match(messageArea.get(GeoParam.WIDTH)));
        
        GuiElement<?> lastElement = null;
        for (RichChatMessage msg : channel.getMessages()) {
            GuiElement<?> container = new GuiElement<>(messageArea.getContentElement());
            container.constrain(GeoParam.LEFT, Constraint.match(messageArea.getContentElement().get(GeoParam.LEFT)));
            container.constrain(GeoParam.RIGHT, Constraint.match(messageArea.getContentElement().get(GeoParam.RIGHT)));
            
            if (lastElement == null) {
                container.constrain(GeoParam.TOP, Constraint.match(messageArea.getContentElement().get(GeoParam.TOP)));
            } else {
                container.constrain(GeoParam.TOP, Constraint.match(lastElement.get(GeoParam.BOTTOM)));
            }

            double currentX = 2;
            
            if (msg.senderIcon() != null) {
                GuiTexture icon = new GuiTexture(container, Material.fromRawTexture(msg.senderIcon()));
                icon.constrain(GeoParam.LEFT, Constraint.relative(container.get(GeoParam.LEFT), currentX))
                    .constrain(GeoParam.TOP, Constraint.relative(container.get(GeoParam.TOP), 2))
                    .constrain(GeoParam.WIDTH, Constraint.literal(8))
                    .constrain(GeoParam.HEIGHT, Constraint.literal(8));
                currentX += 10;
            }

            GuiText msgText = new GuiText(container);
            msgText.setText(net.minecraft.network.chat.Component.literal("<")
                .append(msg.senderName())
                .append("> ")
                .append(msg.content()));
                
            msgText.constrain(GeoParam.LEFT, Constraint.relative(container.get(GeoParam.LEFT), currentX))
                   .constrain(GeoParam.TOP, Constraint.match(container.get(GeoParam.TOP)))
                   .constrain(GeoParam.RIGHT, Constraint.relative(container.get(GeoParam.RIGHT), -2))
                   .setWrap(true).setAlignment(net.creeperhost.polylib.client.modulargui.lib.geometry.Align.MIN).autoHeight();
            
            container.constrain(GeoParam.HEIGHT, Constraint.relative(msgText.get(GeoParam.HEIGHT), 2));
            
            lastElement = container;
        }
    }
}
