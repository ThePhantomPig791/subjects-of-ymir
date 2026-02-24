package net.phantompig.soy.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.network.SetAttackTickerMessage;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.network.TitanAttackAnimationMessage;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.util.EntityUtil;

public class ServerCombatSystem {
    public final ServerPlayer player;
    public SoyPlayerExtension extension;

    public int attackTimer, nextStageTimer, cooldown;

    public int attackStage = 1; // not zero based!!! (because of how armin named the animations, i'm too lazy to change it)
    public AttackType attackType = AttackType.PUNCH;

    public ServerCombatSystem(ServerPlayer player) {
        this.player = player;
        if (player instanceof SoyPlayerExtension ext) {
            extension = ext;
        }
    }

    public enum AttackType {
        PUNCH(3),
        GROUND(3);

        final int maxAttackStage;
        AttackType(int maxAttackStage) {
            this.maxAttackStage = maxAttackStage;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public void attack() {
        if (attackTimer > 0 || cooldown > 0 || extension == null || extension.getTitanInstance().titan == null) {
            attackStage = 1;
            return;
        }

        if (player.getXRot() > 30) {
            attackType = AttackType.GROUND;
        } else {
            attackType = AttackType.PUNCH;
        }

        if (nextStageTimer > 0) {
            if (++attackStage > attackType.maxAttackStage) attackStage = 1;
        } else attackStage = 1;

        SubjectsOfYmir.LOGGER.info("type: {}", attackType);
        String animationId = attackType.toString() + attackStage;
        SubjectsOfYmir.LOGGER.info("anim id: {}", animationId);

        SubjectsOfYmir.LOGGER.info("server entities: {}", player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(150)));
        player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(150)).forEach(pl -> {
            SoyNetwork.NETWORK.sendToPlayer((ServerPlayer) pl, new TitanAttackAnimationMessage(player, animationId));
        });

        player.attackStrengthTicker = -20;
        attackTimer = extension.getTitanInstance().titan.stats.attackSpeed;
        nextStageTimer = attackTimer + 10;
        updateAttackTicker(-attackTimer);
    }

    public void tick() {
        if (attackTimer > 0) attackTimer--;
        if (attackTimer == 12 * 20 / extension.getTitanInstance().titan.stats.attackSpeed) {
            attackEffect();
        }
        if (nextStageTimer > 0) nextStageTimer--;
        if (cooldown > 0) cooldown--;
    }

    public void attackEffect() {
        if (extension == null || extension.getTitanInstance().titan == null) {
            return;
        }

        if (attackType == AttackType.PUNCH) {
            if (attackStage < 3) {
                explodeInFront(1, 5, 0);
            } else {
                explodeInFront(2, 5, 0.1f);

                cooldown = extension.getTitanInstance().titan.stats.attackSpeed * 3 / 2;
                updateAttackTicker(-cooldown);
            }
        }
        if (attackType == AttackType.GROUND) {
            if (attackStage < 3) {
                explodeInFront(1.5f, 6, -0.75f);
            } else {
                explodeInFront(3, 7, -0.75f);

                cooldown = extension.getTitanInstance().titan.stats.attackSpeed * 2;
                updateAttackTicker(-cooldown);
            }
        }
    }

    public void updateAttackTicker(int ticks) {
        SoyNetwork.NETWORK.sendToPlayer(this.player, new SetAttackTickerMessage(ticks));
    }

    public void explodeInFront(float strength, float distance, float endHeightOffset) {
        strength *= (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 5;

        var start = player.getEyePosition();
        var end = player.getLookAngle().multiply(1, 0.5, 1).normalize().scale(distance);
        Vec3 hitPos = EntityUtil.rayTraceWithEntities(player, start, start.add(end).add(0, endHeightOffset * player.getBoundingBox().getYsize(), 0), distance, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, en -> true).getLocation();

        player.level().explode(player, hitPos.x, hitPos.y, hitPos.z, strength, false, Level.ExplosionInteraction.TNT);
    }
}
