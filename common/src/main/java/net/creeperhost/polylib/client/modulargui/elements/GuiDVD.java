package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Rectangle;
import net.creeperhost.polylib.helpers.MathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.Random;
import java.util.function.Consumer;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Just for fun!
 * Created by brandon3055 on 10/09/2023
 */
public class GuiDVD extends GuiElement<GuiDVD> {
    private static final Random randy = new Random();

    private final GuiElement<?> movingElement;
    private double xOffset = 0;
    private double yOffset = 0;
    private Vector2d velocity = null;
    private int bounce = 0;
    private Consumer<Integer> onBounce = bounce -> {};

    public GuiDVD(@NotNull GuiParent<?> parent) {
        super(parent);
        this.movingElement = new GuiElement<>(this)
                .constrain(TOP, Constraint.relative(get(TOP), () -> yOffset))
                .constrain(LEFT, Constraint.relative(get(LEFT), () -> xOffset))
                .constrain(WIDTH, Constraint.match(get(WIDTH)))
                .constrain(HEIGHT, Constraint.match(get(HEIGHT)));
    }

    public GuiElement<?> movingElement() {
        return movingElement;
    }

    public void start() {
        if (velocity == null) {
            velocity = new Vector2d(randy.nextBoolean() ? 1 : -1, randy.nextBoolean() ? 1 : -1);
            velocity.normalize();
        } else {
            velocity = null;
            xOffset = yOffset = 0;
            bounce = 0;
        }
    }

    public void onBounce(Consumer<Integer> onBounce) {
        this.onBounce = onBounce;
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
    }

    @Override
    public void render(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        super.render(render, mouseX, mouseY, partialTicks);
        if (velocity == null) return;

        double speed = 5 * partialTicks;
        xOffset += velocity.x * speed;
        yOffset += velocity.y * speed;
        Rectangle rect = movingElement.getRectangle();

        int bounces = 0;
        if ((velocity.y < 0 && rect.y() < 0) || (velocity.y > 0 && rect.yMax() > scaledScreenHeight())) {
            velocity.y *= -1;
            onBounce.accept(bounce++);
            bounces++;
        }

        if ((velocity.x < 0 && rect.x() < 0) || (velocity.x > 0 && rect.xMax() > scaledScreenWidth())) {
            velocity.x *= -1;
            onBounce.accept(bounce++);
            bounces++;
        }

        if (bounce > 0) {
            velocity.y += -0.05 + (randy.nextGaussian() * 0.1);
            velocity.x += -0.05 + (randy.nextGaussian() * 0.1);
            velocity.x = velocity.x > 0 ? MathUtil.clamp(velocity.x, 0.4, 0.6) : MathUtil.clamp(velocity.x, -0.4, -0.6);
            velocity.y = velocity.y > 0 ? MathUtil.clamp(velocity.y, 0.4, 0.6) : MathUtil.clamp(velocity.y, -0.4, -0.6);
            velocity.normalize();
        }

//        if (bounces == 2)
    }
}
