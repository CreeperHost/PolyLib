package net.creeperhost.testmod.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.creeperhost.polylib.event.events.server.PolyServerCommandEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * /polytest <group> — in-game commands to actively trigger each PolyLib event group.
 *
 * Usage (run as operator):
 *   /polytest living      — heal, knockback, totem check on the executor
 *   /polytest block       — break/place a stone block at feet, trample nearby farmland
 *   /polytest effects     — apply/remove Speed to the executor
 *   /polytest entity      — summon a zombie, mount a pig
 *   /polytest explosion   — summon a TNT explosion at feet
 *   /polytest conversion  — summon a zombie and trigger conversion
 *   /polytest tick        — print the current server tick (confirms PLAYER_TICK events via log)
 */
public final class TestCommands
{
    private static final Logger LOGGER = LogManager.getLogger();

    private TestCommands() {}

    public static void init()
    {
        PolyServerCommandEvents.REGISTER_COMMANDS.register(TestCommands::register);
    }

    private static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext buildContext,
            Commands.CommandSelection selection)
    {
        dispatcher.register(
            Commands.literal("polytest")
                .then(Commands.literal("living").executes(TestCommands::testLiving))
                .then(Commands.literal("block").executes(TestCommands::testBlock))
                .then(Commands.literal("effects").executes(TestCommands::testEffects))
                .then(Commands.literal("entity").executes(TestCommands::testEntity))
                .then(Commands.literal("explosion").executes(TestCommands::testExplosion))
                .then(Commands.literal("conversion").executes(TestCommands::testConversion))
                .then(Commands.literal("tick").executes(TestCommands::testTick))
                .then(Commands.literal("spawn").executes(TestCommands::testSpawn))
                .then(Commands.literal("sleep").executes(TestCommands::testSleep))
                .then(Commands.literal("bow").executes(TestCommands::testBow))
                .then(Commands.literal("xp").executes(TestCommands::testXp))
                // Tier 3+ additions
                .then(Commands.literal("damage").executes(TestCommands::testDamage))
                .then(Commands.literal("fall").executes(TestCommands::testFall))
                .then(Commands.literal("attack").executes(TestCommands::testAttack))
                .then(Commands.literal("equip").executes(TestCommands::testEquip))
                .then(Commands.literal("projectile").executes(TestCommands::testProjectile))
                .then(Commands.literal("lightning").executes(TestCommands::testLightning))
                .then(Commands.literal("teleport").executes(TestCommands::testTeleport))
                .then(Commands.literal("breed").executes(TestCommands::testBreed))
                .then(Commands.literal("split").executes(TestCommands::testSplit))
                .then(Commands.literal("piston").executes(TestCommands::testPiston))
                .then(Commands.literal("noteblock").executes(TestCommands::testNoteBlock))
                .then(Commands.literal("fluid").executes(TestCommands::testFluid))
                .then(Commands.literal("portal").executes(TestCommands::testPortal))
                .then(Commands.literal("gamemode").executes(TestCommands::testGamemode))
                .then(Commands.literal("setspawn").executes(TestCommands::testSetSpawn))
                .then(Commands.literal("item").executes(TestCommands::testItem))
                .then(Commands.literal("useitem").executes(TestCommands::testUseItem))
                .then(Commands.literal("passive").executes(TestCommands::testPassive))
                .then(Commands.literal("manual").executes(TestCommands::testManual))
                .then(Commands.literal("multiplace").executes(TestCommands::testMultiPlace))
                .then(Commands.literal("brewing").executes(TestCommands::testBrewing))
                .then(Commands.literal("help").executes(TestCommands::testHelp))
        );
    }

    // ----- /polytest living -----
    // Triggers: HEAL, KNOCKBACK, USE_TOTEM (passive — USE_TOTEM fires only if player has totem)

    private static int testLiving(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            player.heal(4.0f);  // fires HEAL
            player.knockback(1.0, 1.0, 0.0);  // fires KNOCKBACK
            src.sendSuccess(() -> Component.literal("[polytest] living: heal + knockback fired — check server log"), false);
            src.sendSuccess(() -> Component.literal("Give yourself a Totem and take fatal damage to test USE_TOTEM"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] living: must be a player: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest block -----
    // Triggers: BREAK, PLACE, CROP_GROW_PRE/POST (passive via bonemeal), FARMLAND_TRAMPLE (manual)

    private static int testBlock(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            BlockPos feet = player.blockPosition();

            // Place stone one block above feet
            BlockPos placePos = feet.above(2);
            level.setBlock(placePos, Blocks.STONE.defaultBlockState(), 3);
            src.sendSuccess(() -> Component.literal("[polytest] block: placed stone at " + placePos + " (PLACE event fires via mixin on Fabric, NeoForge bridge)"), false);

            // Break it
            level.destroyBlock(placePos, false, player);
            src.sendSuccess(() -> Component.literal("[polytest] block: broke stone at " + placePos + " (BREAK event)"), false);

            // Plant wheat seeds on farmland nearby so you can use bonemeal to test CROP_GROW
            BlockPos farmPos = feet.north(2);
            BlockPos cropPos = farmPos.above();
            level.setBlock(farmPos, Blocks.FARMLAND.defaultBlockState(), 3);
            level.setBlock(cropPos, Blocks.WHEAT.defaultBlockState(), 3);
            src.sendSuccess(() -> Component.literal("[polytest] block: wheat planted at " + cropPos + " — use /bonemeal or right-click with bonemeal to test CROP_GROW_PRE/POST"), false);

            src.sendSuccess(() -> Component.literal("[polytest] block: FARMLAND_TRAMPLE — run over the farmland at " + farmPos), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] block: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest effects -----
    // Triggers: ALLOW_APPLY, APPLIED, REMOVED, EXPIRED

    private static int testEffects(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();

            // Apply a short Speed effect — fires ALLOW_APPLY, APPLIED
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 0));
            src.sendSuccess(() -> Component.literal("[polytest] effects: applied Speed 1 (3s) — ALLOW_APPLY + APPLIED fired"), false);

            // Remove it immediately — fires REMOVED
            player.removeEffect(MobEffects.SPEED);
            src.sendSuccess(() -> Component.literal("[polytest] effects: removed Speed — REMOVED fired"), false);

            // Apply a 2-tick effect so it expires naturally — fires EXPIRED
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 2, 0));
            src.sendSuccess(() -> Component.literal("[polytest] effects: applied Speed (2t) — watch for EXPIRED in log in ~2 ticks"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] effects: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest entity -----
    // Triggers: JOIN_LEVEL (on spawn), MOUNT (pig spawn + player mounts it)

    private static int testEntity(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(2, 0, 0);

            // Spawn zombie — fires ENTITY_TICK once it ticks, JOIN_LEVEL on add
            Entity zombie = EntityType.ZOMBIE.create(level, EntitySpawnReason.COMMAND);
            if (zombie != null)
            {
                zombie.setPos(pos.x, pos.y, pos.z);
                level.addFreshEntity(zombie);
                src.sendSuccess(() -> Component.literal("[polytest] entity: zombie spawned — JOIN_LEVEL fired"), false);
            }

            // Spawn pig and mount it — fires MOUNT
            Entity pig = EntityType.PIG.create(level, EntitySpawnReason.COMMAND);
            if (pig != null)
            {
                pig.setPos(pos.x + 2, pos.y, pos.z);
                level.addFreshEntity(pig);
                player.startRiding(pig);
                src.sendSuccess(() -> Component.literal("[polytest] entity: pig spawned + mounted — MOUNT fired"), false);
            }
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] entity: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest explosion -----
    // Triggers: START, DETONATE, KNOCKBACK

    private static int testExplosion(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(0, 1, 3);

            // Small explosion nearby — fires START, DETONATE, then KNOCKBACK per-entity
            level.explode(null, pos.x, pos.y, pos.z, 2.0f,
                    net.minecraft.world.level.Level.ExplosionInteraction.TNT);
            src.sendSuccess(() -> Component.literal("[polytest] explosion: detonated at " + pos + " — START/DETONATE/KNOCKBACK fired"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] explosion: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest conversion -----
    // Triggers: CONVERSION_PRE, CONVERSION_POST
    // Spawns a zombie — submerge it in water for ~15s to trigger Zombie -> Drowned conversion

    private static int testConversion(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(2, 0, 2);

            Entity zombie = EntityType.ZOMBIE.create(level, EntitySpawnReason.COMMAND);
            if (zombie != null)
            {
                zombie.setPos(pos.x, pos.y, pos.z);
                level.addFreshEntity(zombie);
                src.sendSuccess(() -> Component.literal("[polytest] conversion: zombie spawned at " + pos), false);
                src.sendSuccess(() -> Component.literal("Submerge the zombie in water for ~15s to trigger CONVERSION_PRE + CONVERSION_POST (Zombie -> Drowned)"), false);
            }
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] conversion: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest tick -----
    // Confirms PLAYER_TICK_START/END are firing (they log every 1200 ticks — check log)

    private static int testTick(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            int tick = (int) (player.level().getGameTime() % Integer.MAX_VALUE);
            src.sendSuccess(() -> Component.literal(
                    "[polytest] tick: current server gametime=" + tick +
                    " | PLAYER_TICK_START/END log every 1200 ticks (watch server log)"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] tick: " + e.getMessage()));
        }
        return 1;
    }

    // =========================================================================
    // Tier 2 test commands
    // =========================================================================

    // ----- /polytest spawn -----
    // Triggers: FINALIZE_SPAWN, MOB_DESPAWN (passive — MOB_DESPAWN fires when far from players)

    private static int testSpawn(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(3, 0, 0);

            // Spawn a skeleton — triggers FINALIZE_SPAWN when it spawns (Mob.finalizeSpawn)
            Entity skeleton = EntityType.SKELETON.create(level, EntitySpawnReason.COMMAND);
            if (skeleton != null)
            {
                skeleton.setPos(pos.x, pos.y, pos.z);
                level.addFreshEntity(skeleton);
                src.sendSuccess(() -> Component.literal("[polytest] spawn: skeleton spawned — FINALIZE_SPAWN fired"), false);
                src.sendSuccess(() -> Component.literal("Move >128 blocks away to trigger MOB_DESPAWN"), false);
            }
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] spawn: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest sleep -----
    // Triggers: ALLOW_SLEEPING prompt (manual), START_SLEEPING, STOP_SLEEPING

    private static int testSleep(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("[polytest] sleep: place a bed and sleep in it to trigger START_SLEEPING, STOP_SLEEPING, and ALLOW_SLEEPING events"), false);
        src.sendSuccess(() -> Component.literal("All three events log to server log when they fire."), false);
        return 1;
    }

    // ----- /polytest bow -----
    // Triggers: ARROW_NOCK (draw bow), ARROW_LOOSE (release bow)

    private static int testBow(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("[polytest] bow: hold a bow and right-click to draw (ARROW_NOCK), then release to fire (ARROW_LOOSE)"), false);
        src.sendSuccess(() -> Component.literal("Both events log to server log when triggered."), false);
        return 1;
    }

    // ----- /polytest xp -----
    // Triggers: XP_CHANGE, XP_LEVEL_CHANGE

    private static int testXp(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            player.giveExperiencePoints(100);    // fires XP_CHANGE
            player.giveExperienceLevels(1);      // fires XP_LEVEL_CHANGE
            src.sendSuccess(() -> Component.literal("[polytest] xp: gave 100 XP points + 1 level — XP_CHANGE + XP_LEVEL_CHANGE fired"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] xp: " + e.getMessage()));
        }
        return 1;
    }

    // =========================================================================
    // Tier 3+ test commands
    // =========================================================================

    // ----- /polytest damage -----
    // Triggers: DAMAGE_PRE, DAMAGE_POST, DAMAGE_FINAL_PRE

    private static int testDamage(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            // Deal 2 hearts of generic damage — fires DAMAGE_PRE, DAMAGE_FINAL_PRE, DAMAGE_POST
            DamageSource src2 = level.damageSources().generic();
            player.hurt(src2, 4.0f);
            src.sendSuccess(() -> Component.literal("[polytest] damage: dealt 2 hearts — DAMAGE_PRE + DAMAGE_FINAL_PRE + DAMAGE_POST fired (check server log)"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] damage: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest fall -----
    // Triggers: FALL (when player lands after teleport up)

    private static int testFall(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            Vec3 pos = player.position();
            // Teleport up 15 blocks — player will fall and land, firing FALL
            player.teleportTo(pos.x, pos.y + 15, pos.z);
            src.sendSuccess(() -> Component.literal("[polytest] fall: teleported up 15 blocks — FALL fires when you land"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] fall: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest attack -----
    // Triggers: ATTACK_ENTITY (attack zombie), CHANGE_TARGET, DEATH + DROPS + XP_DROP (kill it)

    private static int testAttack(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(2, 0, 0);

            // Spawn a zombie with 1 HP so one hit kills it
            Entity zombie = EntityType.ZOMBIE.create(level, EntitySpawnReason.COMMAND);
            if (zombie instanceof net.minecraft.world.entity.LivingEntity living)
            {
                zombie.setPos(pos.x, pos.y, pos.z);
                level.addFreshEntity(zombie);
                // Reduce HP to 1 so a single attack will kill it → DEATH + DROPS + XP_DROP
                living.setHealth(1.0f);
                src.sendSuccess(() -> Component.literal("[polytest] attack: zombie spawned at " + pos + " with 1 HP"), false);
                src.sendSuccess(() -> Component.literal("Attack it to fire: ATTACK_ENTITY, CHANGE_TARGET, CRITICAL_HIT (jump+hit), DEATH, DROPS, XP_DROP"), false);
            }
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] attack: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest equip -----
    // Triggers: ENTITY_EQUIP_CHANGE (give iron armor to player → they equip it), SWAP_ITEMS (press F)

    private static int testEquip(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            // Give iron armor to inventory — equipping it fires ENTITY_EQUIP_CHANGE
            player.getInventory().add(new ItemStack(Items.IRON_HELMET));
            player.getInventory().add(new ItemStack(Items.IRON_CHESTPLATE));
            player.getInventory().add(new ItemStack(Items.IRON_SWORD));
            player.getInventory().add(new ItemStack(Items.BOW));
            src.sendSuccess(() -> Component.literal("[polytest] equip: gave iron helmet, chestplate, sword, bow"), false);
            src.sendSuccess(() -> Component.literal("Put armor on to fire ENTITY_EQUIP_CHANGE. Press F with sword in hand to fire SWAP_ITEMS."), false);
            src.sendSuccess(() -> Component.literal("Hold bow + right-click to fire ARROW_NOCK, release for ARROW_LOOSE + GET_PROJECTILE."), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] equip: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest projectile -----
    // Triggers: PROJECTILE_IMPACT (arrow hits stone wall), GET_PROJECTILE (entity shoots)

    private static int testProjectile(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position();

            // Place a stone wall 5 blocks in front
            BlockPos wallPos = player.blockPosition().relative(player.getDirection(), 5);
            level.setBlock(wallPos, Blocks.STONE.defaultBlockState(), 3);

            // Shoot an arrow toward the wall
            Entity arrowEntity = EntityType.ARROW.create(level, EntitySpawnReason.COMMAND);
            Vec3 look = player.getLookAngle();
            if (arrowEntity instanceof Projectile arrowProj)
            {
                arrowProj.setOwner(player);
                arrowProj.setPos(pos.x, pos.y + 1.5, pos.z);
                arrowProj.shoot(look.x, look.y, look.z, 1.5f, 0f);
                level.addFreshEntity(arrowProj);
            }

            src.sendSuccess(() -> Component.literal("[polytest] projectile: arrow fired toward stone wall at " + wallPos + " — PROJECTILE_IMPACT fires on hit"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] projectile: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest lightning -----
    // Triggers: STRUCK_BY_LIGHTNING (entity hit), DESTROY_BLOCK (creeper explosion nearby)

    private static int testLightning(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(3, 0, 3);

            // Spawn a pig as the strike target
            Entity pig = EntityType.PIG.create(level, EntitySpawnReason.COMMAND);
            if (pig != null)
            {
                pig.setPos(pos.x, pos.y, pos.z);
                level.addFreshEntity(pig);
            }

            // Create lightning bolt at that position
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            bolt.setPos(pos.x, pos.y, pos.z);
            level.addFreshEntity(bolt);

            src.sendSuccess(() -> Component.literal("[polytest] lightning: lightning bolt + pig at " + pos + " — STRUCK_BY_LIGHTNING fires on hit"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] lightning: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest teleport -----
    // Triggers: TELEPORT (entity teleports within level)

    private static int testTeleport(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 spawnPos = player.position().add(2, 0, 0);

            // Spawn an enderman and teleport it — fires TELEPORT
            Entity enderman = EntityType.ENDERMAN.create(level, EntitySpawnReason.COMMAND);
            if (enderman != null)
            {
                enderman.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                level.addFreshEntity(enderman);
                Vec3 dest = spawnPos.add(5, 0, 5);
                enderman.teleportTo(dest.x, dest.y, dest.z);
                src.sendSuccess(() -> Component.literal("[polytest] teleport: enderman spawned + teleported — TELEPORT fired"), false);
                src.sendSuccess(() -> Component.literal("Also fires LEAVE_LEVEL when despawned (walk away >128 blocks)"), false);
            }
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] teleport: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest breed -----
    // Triggers: BABY_SPAWN (two animals breed)

    private static int testBreed(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(2, 0, 0);

            // Spawn two adult pigs and put them in love mode — fires BABY_SPAWN when baby is created
            for (int i = 0; i < 2; i++)
            {
                Entity pig = EntityType.PIG.create(level, EntitySpawnReason.COMMAND);
                if (pig instanceof Animal p)
                {
                    p.setPos(pos.x + i, pos.y, pos.z);
                    p.setAge(0); // adult
                    p.setInLove(player);  // triggers love mode
                    level.addFreshEntity(p);
                }
            }
            src.sendSuccess(() -> Component.literal("[polytest] breed: two pigs in love spawned — BABY_SPAWN fires when they breed (~2.5s)"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] breed: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest split -----
    // Triggers: MOB_SPLIT (slime splits when damaged)

    private static int testSplit(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(3, 0, 0);

            // Spawn a large slime (size 4) — fires MOB_SPLIT when killed
            Entity slime = EntityType.SLIME.create(level, EntitySpawnReason.COMMAND);
            if (slime instanceof net.minecraft.world.entity.monster.Slime s)
            {
                s.setPos(pos.x, pos.y, pos.z);
                s.setSize(4, true); // large slime — will split when killed
                level.addFreshEntity(s);
                src.sendSuccess(() -> Component.literal("[polytest] split: large slime (size 4) spawned — MOB_SPLIT fires when you kill it"), false);
            }
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] split: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest piston -----
    // Triggers: PISTON_PRE, PISTON_POST

    private static int testPiston(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            BlockPos feet = player.blockPosition();

            // Place piston facing up + redstone block behind it to power immediately
            BlockPos pistonPos = feet.north(4).above();
            BlockPos redstonePos = pistonPos.north(1);
            level.setBlock(pistonPos, Blocks.PISTON.defaultBlockState()
                    .setValue(PistonBaseBlock.FACING, Direction.UP), 3);
            level.setBlock(redstonePos, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            src.sendSuccess(() -> Component.literal("[polytest] piston: piston + redstone block placed at " + pistonPos + " — PISTON_PRE + PISTON_POST should fire on extension"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] piston: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest noteblock -----
    // Triggers: NOTE_BLOCK_PLAY (hit the note block)

    private static int testNoteBlock(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            BlockPos pos = player.blockPosition().north(2).above();
            level.setBlock(pos.below(), Blocks.DIRT.defaultBlockState(), 3); // base
            level.setBlock(pos, Blocks.NOTE_BLOCK.defaultBlockState(), 3);
            src.sendSuccess(() -> Component.literal("[polytest] noteblock: note block placed at " + pos), false);
            src.sendSuccess(() -> Component.literal("Left-click it (or use /trigger note_block) to fire NOTE_BLOCK_PLAY"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] noteblock: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest fluid -----
    // Triggers: FLUID_SOURCE (water converts to source block)

    private static int testFluid(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            BlockPos base = player.blockPosition().north(4);

            // Dig a 2-wide trench and place water on both ends — fluid meeting in middle creates source
            for (int dx = -1; dx <= 2; dx++)
            {
                level.setBlock(base.east(dx), Blocks.AIR.defaultBlockState(), 3);
            }
            level.setBlock(base.west(1), Blocks.WATER.defaultBlockState(), 3);
            level.setBlock(base.east(2), Blocks.WATER.defaultBlockState(), 3);
            src.sendSuccess(() -> Component.literal("[polytest] fluid: water placed on both sides of trench at " + base + " — FLUID_SOURCE fires when they meet in the middle"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] fluid: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest portal -----
    // Triggers: PORTAL_SPAWN (light the nether portal)

    private static int testPortal(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            BlockPos origin = player.blockPosition().north(5);

            // Build a 4x5 obsidian portal frame
            for (int y = 0; y <= 4; y++)
                for (int x = 0; x <= 3; x++)
                {
                    boolean frame = (x == 0 || x == 3 || y == 0 || y == 4);
                    if (frame) level.setBlock(origin.east(x).above(y), Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            src.sendSuccess(() -> Component.literal("[polytest] portal: obsidian frame built at " + origin), false);
            src.sendSuccess(() -> Component.literal("Use flint-and-steel on the inside to fire PORTAL_SPAWN"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] portal: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest gamemode -----
    // Triggers: CHANGE_GAME_MODE (switches to spectator then back to survival)

    private static int testGamemode(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            GameType current = player.gameMode.getGameModeForPlayer();
            // Switch to spectator then back — fires CHANGE_GAME_MODE twice
            player.setGameMode(GameType.SPECTATOR);
            player.setGameMode(current);
            src.sendSuccess(() -> Component.literal("[polytest] gamemode: switched to SPECTATOR and back — CHANGE_GAME_MODE fired twice (check log)"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] gamemode: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest setspawn -----
    // Triggers: SET_SPAWN

    private static int testSetSpawn(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            BlockPos pos = player.blockPosition();
            // Set and then clear the spawn point — fires SET_SPAWN twice
            player.setRespawnPosition(
                new ServerPlayer.RespawnConfig(
                    LevelData.RespawnData.of(player.level().dimension(), pos, player.getYRot(), 0.0f),
                    false
                ),
                true
            );
            src.sendSuccess(() -> Component.literal("[polytest] setspawn: spawn set to " + pos + " — SET_SPAWN fired (check log)"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] setspawn: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest item -----
    // Triggers: ITEM_PICKUP (walk over item), ITEM_EXPIRE (after 5 min), hint for ITEM_TOSS

    private static int testItem(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            Vec3 pos = player.position().add(1, 0.5, 0);

            // Drop 5 dirt items on the ground — player walks over to trigger ITEM_PICKUP
            net.minecraft.world.entity.item.ItemEntity item = new net.minecraft.world.entity.item.ItemEntity(
                    level, pos.x, pos.y, pos.z, new ItemStack(Items.DIRT, 5));
            item.setNoPickUpDelay();
            level.addFreshEntity(item);

            src.sendSuccess(() -> Component.literal("[polytest] item: 5 dirt dropped at " + pos), false);
            src.sendSuccess(() -> Component.literal("Walk over it → ITEM_PICKUP fires. Press Q on an item in your inventory → ITEM_TOSS fires."), false);
            src.sendSuccess(() -> Component.literal("ITEM_EXPIRE fires automatically after ~5 minutes if not picked up."), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] item: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest useitem -----
    // Triggers: USE_ITEM (PlayerEvents), ITEM_USE_START, ITEM_USE_TICK, ITEM_USE_FINISH (LivingEvents + ItemEvents)

    private static int testUseItem(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            player.getInventory().add(new ItemStack(Items.BREAD, 8));
            player.getInventory().add(new ItemStack(Items.BOW, 1));
            src.sendSuccess(() -> Component.literal("[polytest] useitem: gave bread x8 and a bow"), false);
            src.sendSuccess(() -> Component.literal("Right-click to eat bread (empty stomach) → USE_ITEM + ITEM_USE_START + ITEM_USE_TICK (per tick) + ITEM_USE_FINISH"), false);
            src.sendSuccess(() -> Component.literal("Hold right-click with bow → ITEM_USE_START + ITEM_USE_TICK each tick; release → ITEM_USE_FINISH + ARROW_LOOSE"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] useitem: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest passive -----
    // Lists all events that fire automatically without any command

    private static int testPassive(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§a[polytest] Passive/lifecycle events (fire automatically):"), false);
        src.sendSuccess(() -> Component.literal("  §7SERVER_STARTED/STOPPING/STOPPED §f— on server start/stop"), false);
        src.sendSuccess(() -> Component.literal("  §7LEVEL_LOAD/UNLOAD/SAVE §f— on world load/save"), false);
        src.sendSuccess(() -> Component.literal("  §7CHUNK_LOAD/CHUNK_UNLOAD §f— move through the world"), false);
        src.sendSuccess(() -> Component.literal("  §7TICK_START/TICK_END, LEVEL_TICK_* §f— fires every tick (logged every 1200t)"), false);
        src.sendSuccess(() -> Component.literal("  §7PLAYER_TICK_START/END §f— fires every player tick (logged every 1200t)"), false);
        src.sendSuccess(() -> Component.literal("  §7ENTITY_TICK §f— fires every entity tick (logged every 10000t per entity)"), false);
        src.sendSuccess(() -> Component.literal("  §7LOGIN/LOGOUT/RESPAWN/CLONE §f— connect, disconnect, die+respawn"), false);
        src.sendSuccess(() -> Component.literal("  §7START_TRACKING §f— come within range of another player"), false);
        src.sendSuccess(() -> Component.literal("  §7ENTITY_SOUND §f— fires on any entity sound"), false);
        src.sendSuccess(() -> Component.literal("  §7BREATHE §f— fires every underwater tick (debug level)"), false);
        src.sendSuccess(() -> Component.literal("  §7SLEEP_FINISHED §f— wait for night to end after sleeping"), false);
        src.sendSuccess(() -> Component.literal("  §7TAGS_UPDATED §f— run /reload"), false);
        src.sendSuccess(() -> Component.literal("  §7LEAVE_LEVEL §f— entity removed from world"), false);
        src.sendSuccess(() -> Component.literal("  §7CHANGE_DIMENSION/TRAVEL_DIMENSION §f— go through a portal"), false);
        src.sendSuccess(() -> Component.literal("  §7CONTAINER_CLOSE §f— open then close any container"), false);
        src.sendSuccess(() -> Component.literal("  §7RIGHT_CLICK_BLOCK §f— right-click any block"), false);
        src.sendSuccess(() -> Component.literal("  §7BREAK_BLOCK §f— mine a block manually"), false);
        src.sendSuccess(() -> Component.literal("  §7ATTACK_ENTITY §f— hit any mob"), false);
        src.sendSuccess(() -> Component.literal("  §7ENTITY_INTERACT §f— right-click a mob"), false);
        src.sendSuccess(() -> Component.literal("  §7XP_PICKUP §f— walk over an XP orb"), false);
        src.sendSuccess(() -> Component.literal("  §7CROP_GROW_POST §f— use bonemeal on wheat from /polytest block"), false);
        return 1;
    }

    // ----- /polytest manual -----
    // Lists events that require specific manual gameplay to trigger

    private static int testManual(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§e[polytest] Events requiring manual gameplay:"), false);
        src.sendSuccess(() -> Component.literal("  §7ANIMAL_TAME §f— find a wolf, feed it bones until tamed"), false);
        src.sendSuccess(() -> Component.literal("  §7ITEM_CRAFTED §f— open a crafting table and craft any item"), false);
        src.sendSuccess(() -> Component.literal("  §7ITEM_FISHING §f— equip a fishing rod, right-click water, reel in"), false);
        src.sendSuccess(() -> Component.literal("  §7ANVIL_UPDATE §f— open an anvil and place items inside"), false);
        src.sendSuccess(() -> Component.literal("  §7GRINDSTONE §f— open a grindstone and place enchanted items"), false);
        src.sendSuccess(() -> Component.literal("  §7SHIELD_BLOCK §f— hold a shield, get hit by a mob or arrow"), false);
        src.sendSuccess(() -> Component.literal("  §7CRITICAL_HIT §f— jump and attack a mob while airborne"), false);
        src.sendSuccess(() -> Component.literal("  §7DESTROY_BLOCK §f— let a creeper/enderman destroy a block"), false);
        src.sendSuccess(() -> Component.literal("  §7MOB_GRIEFING §f— wait for a creeper to explode near blocks"), false);
        src.sendSuccess(() -> Component.literal("  §7USE_TOTEM §f— carry a Totem of Undying and take fatal damage"), false);
        src.sendSuccess(() -> Component.literal("  §7BABY_SPAWN (also /polytest breed) §f— feed two same animals"), false);
        src.sendSuccess(() -> Component.literal("  §7DROWN §f— stay submerged in water until air runs out"), false);
        src.sendSuccess(() -> Component.literal("  §7PLAYER DEATH events §f— take enough damage to die"), false);
        src.sendSuccess(() -> Component.literal("  §7NOTE_BLOCK_PLAY §f— (also /polytest noteblock) §fhit a note block"), false);
        src.sendSuccess(() -> Component.literal("  §7PORTAL_SPAWN §f— (also /polytest portal) §flight the frame"), false);
        return 1;
    }

    // ----- /polytest multiplace -----
    // Triggers: ENTITY_MULTI_PLACE (door or bed placed = two blocks at once)

    private static int testMultiPlace(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            BlockPos pos = player.blockPosition().above();
            // Place a door — placing a door creates two BlockStates (lower + upper) which fires ENTITY_MULTI_PLACE
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.OAK_DOOR.defaultBlockState(), 3);
            level.setBlock(pos.above(), net.minecraft.world.level.block.Blocks.OAK_DOOR.defaultBlockState()
                    .setValue(net.minecraft.world.level.block.DoorBlock.HALF,
                              net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), 3);
            src.sendSuccess(() -> Component.literal("[polytest] multiplace: oak door placed at " + pos + " — ENTITY_MULTI_PLACE fires on multi-block placements (doors, beds)"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] multiplace: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest brewing -----
    // Triggers: BREW_PRE and BREW_POST (fires when a brewing stand completes a brew)
    // Note: BREW_PRE/POST are observer-only here — player must actually use a brewing stand.
    // This command gives the player a blaze powder + potions to make brewing easy.

    private static int testBrewing(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        try
        {
            ServerPlayer player = src.getPlayerOrException();
            // Give ingredients to brew: blaze powder (fuel), awkward potion, nether wart
            player.getInventory().add(new ItemStack(Items.BLAZE_POWDER, 4));
            player.getInventory().add(new ItemStack(Items.NETHER_WART, 4));
            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE, 3));
            player.getInventory().add(new ItemStack(Items.GUNPOWDER, 2));
            src.sendSuccess(() -> Component.literal(
                    "[polytest] brewing: gave ingredients — place a brewing stand, fill with water bottles + nether wart, fuel with blaze powder to fire BREW_PRE / BREW_POST"), false);
        }
        catch (Exception e)
        {
            src.sendFailure(Component.literal("[polytest] brewing: " + e.getMessage()));
        }
        return 1;
    }

    // ----- /polytest help -----

    private static int testHelp(CommandContext<CommandSourceStack> ctx)
    {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§b[polytest] Available subcommands:"), false);
        src.sendSuccess(() -> Component.literal("  §fTier 1: §7living, block, effects, entity, explosion, conversion, tick, spawn, sleep, bow, xp"), false);
        src.sendSuccess(() -> Component.literal("  §fTier 3+: §7damage, fall, attack, equip, projectile, lightning, teleport, breed, split, piston, noteblock, fluid, portal, gamemode, setspawn, item, useitem, multiplace, brewing"), false);
        src.sendSuccess(() -> Component.literal("  §fInfo: §7passive (auto-firing events), manual (gameplay-required events), help"), false);
        return 1;
    }
}
