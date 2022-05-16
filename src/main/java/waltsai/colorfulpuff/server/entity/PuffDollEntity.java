package waltsai.colorfulpuff.server.entity;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import waltsai.colorfulpuff.ModProvider;
import waltsai.colorfulpuff.client.style.ClothStyle;
import waltsai.colorfulpuff.client.style.EyesStyle;

import java.util.*;

public class PuffDollEntity extends Entity {
    public static final TrackedData<Integer> CLOTH_TYPE;
    public static final TrackedData<Integer> EYE_TYPE;
    public static final TrackedData<Boolean> SITTING;

    private Random random;

    public PuffDollEntity(EntityType<?> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
        this.random = new Random();
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(CLOTH_TYPE, 0);
        this.dataTracker.startTracking(EYE_TYPE, 0);
        this.dataTracker.startTracking(SITTING, false);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setClothType(ClothType.byId(nbt.getInt("ClothType")));
        this.setEyeType(EyeType.byId(nbt.getInt("EyeType")));
        this.setSitting(nbt.getBoolean("Sitting"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("ClothType", this.getClothType().getId());
        nbt.putInt("EyeType", this.getEyeType().getId());
        nbt.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }
    }

    public ClothType getClothType() {
        return ClothType.byId(dataTracker.get(CLOTH_TYPE));
    }

    public EyeType getEyeType() {
        return EyeType.byId(dataTracker.get(EYE_TYPE));
    }

    public boolean havingCloth(ClothType... type) {
        return Arrays.stream(type).filter((e) -> e == this.getClothType()).toArray().length > 0;
    }

    public boolean havingEye(EyeType... type) {
        return Arrays.stream(type).filter((e) -> e == this.getEyeType()).toArray().length > 0;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        ClothType cloth = ClothType.byItem(item);
        EyeType eye = EyeType.byItem(item);
        if (itemStack.isOf(Items.POTION) && PotionUtil.getPotionEffects(itemStack).size() == 0 && this.getClothType() == ClothType.SHRIVEL_JASMINE) {
            player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE, 1));
            itemStack.decrement(1);
            this.setClothType(ClothType.JASMINE);
            this.world.sendEntityStatus(this, (byte) 84);
            return ActionResult.SUCCESS;
        }
        if (cloth != null && !this.havingCloth(cloth)) {
            this.setClothType(cloth);
            this.world.sendEntityStatus(this, (byte) (61 + cloth.getId()));
            if (!player.getAbilities().creativeMode) {
                if ((cloth.getItemToDye() instanceof PotionItem && PotionUtil.getPotionEffects(itemStack).size() == 0) || cloth.getItemToDye() instanceof HoneyBottleItem) {
                    player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE, 1));
                }
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        if (eye != null && !this.havingEye(eye)) {
            this.setEyeType(eye);
            this.world.sendEntityStatus(this, (byte) (eye.getId()));
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        ActionResult actionResult2 = super.interact(player, hand);
        if (!actionResult2.isAccepted()) {
            if (player.isSneaking()) {
                this.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, player.getEyePos());
                return ActionResult.SUCCESS;
            }
            this.setSitting(!this.isSitting());
            return ActionResult.SUCCESS;
        }
        return actionResult2;
    }

    public void setClothType(@Nullable ClothType type) {
        if(type == null) {
            dataTracker.set(CLOTH_TYPE, ClothType.UNDYED.getId());
            return;
        }
        dataTracker.set(CLOTH_TYPE, type.getId());
    }

    public void setEyeType(@Nullable EyeType type) {
        if(type == null) {
            dataTracker.set(EYE_TYPE, EyeType.EYE1.getId());
            return;
        }
        dataTracker.set(EYE_TYPE, type.getId());
    }

    public boolean isSitting() {
        return dataTracker.get(SITTING);
    }


    public void setSitting(boolean sitting) {
        dataTracker.set(SITTING, sitting);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        ItemStack doll = new ItemStack(ModProvider.PUFF_DOLL_SPAWN_EGG);
        NbtCompound dollNbt = new NbtCompound();
        this.writeNbt(dollNbt);
        doll.getOrCreateNbt().put("EntityTag", dollNbt);
        if (source.getAttacker() != null && source.getAttacker().isPlayer() && ((PlayerEntity)source.getAttacker()).getMainHandStack().isEmpty()) {
            ((PlayerEntity) source.getAttacker()).setStackInHand(Hand.MAIN_HAND, doll);
        } else {
            this.dropStack(doll);
        }
        this.remove(RemovalReason.KILLED);
        return true;
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if ((status > 60 && status < 77) || (status > 77 && status <= 80) || status == 82 || status == 84 || status == 85) {
            float alpha = this.random.nextFloat() * 0.25F + 0.75F;
            this.produceParticles(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(ClothType.byId(status - 61).getColorCode())), alpha), 8);
        }
        if (status == 77) {
            this.produceParticles(ParticleTypes.CLOUD, 8);
        }
        if (status == 81) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER, 12);
        }
        if (status == 83) {
            this.produceParticles(ParticleTypes.SMOKE, 24);
        }
        if (status == 86) {
            this.produceParticles(ParticleTypes.LARGE_SMOKE, 6);
        }

        if (status >= 0 && status < EyeType.values().length) {
            float alpha = this.random.nextFloat() * 0.25F + 0.75F;
            for (EyesStyle color : EyeType.byId(status).getParticles()) {
                this.produceDyestuffParticles(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(color.style().getColor().getRgb())), alpha), EyeType.byId(status).getParticles().size() > 1 ? 4 : 8);
            }
        }
    }

    protected void produceParticles(ParticleEffect parameters, int count) {
        for(int i = 0; i < count; ++i) {
            double d = this.random.nextGaussian() * 0.02D;
            double e = this.random.nextGaussian() * 0.02D;
            double f = this.random.nextGaussian() * 0.02D;
            this.world.addParticle(parameters, this.getParticleX(1.0D), this.getY(), this.getParticleZ(1.0D), d, e, f);
        }
    }

    protected void produceDyestuffParticles(ParticleEffect parameters, int count) {
        for(int i = 0; i < count; ++i) {
            double d = this.random.nextGaussian() * 0.02D;
            double e = this.random.nextGaussian() * 0.02D;
            double f = this.random.nextGaussian() * 0.02D;
            this.world.addParticle(parameters, this.getParticleX(1.0D), this.getY() + 0.6f, this.getParticleZ(1.0D), d, e, f);
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    static {
        CLOTH_TYPE = DataTracker.registerData(PuffDollEntity.class, TrackedDataHandlerRegistry.INTEGER);
        EYE_TYPE = DataTracker.registerData(PuffDollEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SITTING = DataTracker.registerData(PuffDollEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    public enum ClothType {
        WHITE(0, "white", 16383998, Items.WHITE_DYE, ClothStyle.WHITE.style()),
        ORANGE(1, "orange", 16351261, Items.ORANGE_DYE, ClothStyle.ORANGE.style()),
        MAGENTA(2, "magenta", 13061821, Items.MAGENTA_DYE, ClothStyle.MAGENTA.style()),
        LIGHT_BLUE(3, "light_blue", 3847130, Items.LIGHT_BLUE_DYE, ClothStyle.LIGHT_BLUE.style()),
        YELLOW(4, "yellow", 16701501, Items.YELLOW_DYE, ClothStyle.YELLOW.style()),
        LIME(5, "lime", 8439583, Items.LIME_DYE, ClothStyle.LIME.style()),
        PINK(6, "pink", 15961002, Items.PINK_DYE, ClothStyle.PINK.style()),
        GRAY(7, "gray", 4673362, Items.GRAY_DYE, ClothStyle.GRAY.style()),
        SILVER(8, "silver", 10329495, Items.LIGHT_GRAY_DYE, ClothStyle.SILVER.style()),
        CYAN(9, "cyan", 1481884, Items.CYAN_DYE, ClothStyle.CYAN.style()),
        PURPLE(10, "purple", 8991416, Items.PURPLE_DYE, ClothStyle.PURPLE.style()),
        BLUE(11, "blue", 3949738, Items.BLUE_DYE, ClothStyle.BLUE.style()),
        BROWN(12, "brown", 8606770, Items.BROWN_DYE, ClothStyle.BROWN.style()),
        GREEN(13, "green", 6192150, Items.GREEN_DYE, ClothStyle.GREEN.style()),
        RED(14, "red", 11546150, Items.RED_DYE, ClothStyle.RED.style()),
        BLACK(15, "black", 1908001, Items.BLACK_DYE, ClothStyle.BLACK.style()),
        UNDYED(16, "undyed", -1, Items.POTION, ClothStyle.UNDYED.style()),
        HONEY(17, "honey", 13408512, Items.HONEY_BOTTLE, ClothStyle.HONEY.style()),
        NECTAR(18, "nectar", 16764281, Items.HONEY_BLOCK, ClothStyle.NECTAR.style()),
        CAREMAL(19, "caramel", 10243339, Items.FURNACE, ClothStyle.CAREMAL.style()),
        MIFU(20, "mifu", -1, Items.NAME_TAG, ClothStyle.MIFU.style()),
        ROSEUS(21, "roseus", 12976128, Items.ROSE_BUSH, ClothStyle.ROSEUS.style()),
        WITHER(22, "wither", -1, Items.WITHER_ROSE, ClothStyle.WITHER.style()),
        JASMINE(23, "jasmin", 13626814, null, ClothStyle.JASMIN.style()),
        SHRIVEL_JASMINE(24, "shrivel_jasmin", 14413264, Items.VINE, ClothStyle.SHRIVEL_JASMINE.style()),
        ZOMBIFIED(25, "zombified", -1, Items.ROTTEN_FLESH, ClothStyle.ZOMBIFIED.style());

        private final int id;
        private final String name;
        private final int colorCode;
        private final Item itemToDye;
        private final Style style;

        ClothType(int id, String name, int colorCode, @Nullable Item itemToDye, Style style) {
            this.id = id;
            this.name = name;
            this.colorCode = colorCode;
            this.itemToDye = itemToDye;
            this.style = style;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public int getColorCode() {
            return this.colorCode;
        }

        @Nullable
        public Item getItemToDye() {
            return this.itemToDye;
        }

        public Style getStyle() {
            return this.style;
        }

        public static ClothType byId(int id) {
            for(ClothType type : values()) {
                if(type.getId() == id) {
                    return type;
                }
            }
            return null;
        }

        public static ClothType byItem(Item item) {
            for(ClothType type : values()) {
                if(type.getItemToDye() != null && type.getItemToDye() == item) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum EyeType {
        EYE1(0, "both_blue", sameColorEye(EyesStyle.LIGHT_BLUE), ModProvider.DYESTUFF_BOTH_BLUE, EyesStyle.LIGHT_BLUE),
        EYE2(1, "both_chetwode", sameColorEye(EyesStyle.CHETWODE), ModProvider.DYESTUFF_BOTH_CHETWODE, EyesStyle.CHETWODE),
        EYE3(2, "both_green", sameColorEye(EyesStyle.GREEN), ModProvider.DYESTUFF_BOTH_GREEN, EyesStyle.GREEN),
        EYE4(3, "both_lavender", sameColorEye(EyesStyle.LAVENDER), ModProvider.DYESTUFF_BOTH_LAVENDER, EyesStyle.LAVENDER),
        EYE5(4, "blue_chetwode", combinedEye(EyesStyle.LIGHT_BLUE, EyesStyle.CHETWODE), ModProvider.DYESTUFF_BLUE_CHETWODE, EyesStyle.LIGHT_BLUE, EyesStyle.CHETWODE),
        EYE6(5, "blue_dark", combinedEye(EyesStyle.LIGHT_BLUE, EyesStyle.DARK_BLUE), ModProvider.DYESTUFF_BLUE_DARK, EyesStyle.LIGHT_BLUE, EyesStyle.DARK_BLUE),
        EYE7(6, "blue_yellow", combinedEye(EyesStyle.LIGHT_BLUE, EyesStyle.YELLOW), ModProvider.DYESTUFF_BLUE_YELLOW, EyesStyle.LIGHT_BLUE, EyesStyle.YELLOW),
        EYE8(7, "both_downy", sameColorEye(EyesStyle.DOWNY), ModProvider.DYESTUFF_BOTH_DOWNY, EyesStyle.DOWNY),
        EYE9(8, "both_rose", sameColorEye(EyesStyle.ROSE), ModProvider.DYESTUFF_BOTH_ROSE, EyesStyle.ROSE),
        EYE10(9, "both_puce", sameColorEye(EyesStyle.PUCE), ModProvider.DYESTUFF_BOTH_PUCE, EyesStyle.PUCE),
        EYE11(10, "both_purple", sameColorEye(EyesStyle.PURPLE), ModProvider.DYESTUFF_BOTH_PURPLE, EyesStyle.PURPLE),
        BOTH_JASMINE(11, "both_jasmine", sameColorEye(EyesStyle.SHRIVEL_JASMINE), ModProvider.DYESTUFF_BOTH_JASMINE, EyesStyle.SHRIVEL_JASMINE),
        ROSEUS(12, "roseus_special", combinedEye(EyesStyle.ROSE, EyesStyle.ROSEUS_SPECIAL), ModProvider.DYESTUFF_ROSEUS, EyesStyle.ROSE, EyesStyle.ROSEUS_SPECIAL),
        WITHER(13, "wither", sameColorEye(EyesStyle.WITHER), ModProvider.DYESTUFF_WITHER, EyesStyle.WITHER),
        ZOMBIFIED(14, "zombified", sameColorEye(EyesStyle.ZOMBIFIED), ModProvider.DYESTUFF_ZOMBIFIED, EyesStyle.ZOMBIFIED),
        MIFU(15, "mifu", sameColorEye(EyesStyle.MIFU), ModProvider.DYESTUFF_MIFU, EyesStyle.MIFU);


        private final int id;
        private final String name;
        private final Text text;
        private final Item itemToDye;
        private final ArrayList<EyesStyle> particle = new ArrayList<>();

        EyeType(int id, String name, Text text, Item itemToDye, EyesStyle... particles) {
            this.id = id;
            this.name = name;
            this.text = text;
            this.itemToDye = itemToDye;
            this.particle.addAll(Arrays.stream(particles).toList());
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public Text getTooltip() {
            return this.text;
        }

        public Item getItemToDye() {
            return this.itemToDye;
        }

        public ArrayList<EyesStyle> getParticles() {
            return this.particle;
        }

        public static EyeType byId(int id) {
            for(EyeType type : values()) {
                if(type.getId() == id) {
                    return type;
                }
            }
            return null;
        }

        public static EyeType byItem(Item item) {
            for(EyeType type : values()) {
                if(type.getItemToDye() != null && type.getItemToDye() == item) {
                    return type;
                }
            }
            return null;
        }

        private static Text sameColorEye(EyesStyle style) {
            return new TranslatableText("eyes.colorfulpuff.puff_doll.title").append(" [").append(new TranslatableText("eyes.colorfulpuff.puff_doll.both").append(" ").append(new TranslatableText("eyes.colorfulpuff.puff_doll." + style.name().toLowerCase()).setStyle(style.style()))).append(new LiteralText("]").setStyle(Style.EMPTY));
        }

        private static Text combinedEye(EyesStyle style1, EyesStyle style2) {
            return new TranslatableText("eyes.colorfulpuff.puff_doll.title").append(" [").append(new TranslatableText("eyes.colorfulpuff.puff_doll." + style1.name().toLowerCase()).setStyle(style1.style())).append(new LiteralText(" & ").setStyle(Style.EMPTY)).append(new TranslatableText("eyes.colorfulpuff.puff_doll." + style2.name().toLowerCase()).setStyle(style2.style())).append(new LiteralText("]").setStyle(Style.EMPTY));
        }
    }
}
