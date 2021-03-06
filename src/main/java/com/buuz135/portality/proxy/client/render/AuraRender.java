package com.buuz135.portality.proxy.client.render;

import com.buuz135.portality.Portality;
import com.hrznstudio.titanium.reward.storage.ClientRewardStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

public class AuraRender extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private PlayerModel model = new PlayerModel(0.4f, false);

    public AuraRender(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, AbstractClientPlayerEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_) {
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().containsKey(entityIn.getUniqueID())) return;
        if (!ClientRewardStorage.REWARD_STORAGE.getRewards().get(entityIn.getUniqueID()).getEnabled().containsKey(new ResourceLocation(Portality.MOD_ID, "aura")))
            return;
        Portality.AuraType type = Portality.AuraType.valueOf(ClientRewardStorage.REWARD_STORAGE.getRewards().get(entityIn.getUniqueID()).getEnabled().get(new ResourceLocation(Portality.MOD_ID, "aura")));
        RenderSystem.pushMatrix();
        boolean flag = entityIn.isInvisible();
        RenderSystem.depthMask(!flag);
        Minecraft.getInstance().getTextureManager().bindTexture(type.getResourceLocation());
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = (float) entityIn.ticksExisted + p_212842_4_;
        RenderSystem.translatef(0, f * 0.01F, 0.0F);
        RenderSystem.matrixMode(5888);
        RenderSystem.enableBlend();
        float f1 = 0.5F;
        RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
        RenderSystem.disableLighting();
        if (type.isEnableBlend())
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        this.getEntityModel().setModelAttributes(model);
        GameRenderer gamerenderer = Minecraft.getInstance().gameRenderer;
        RenderSystem.setupGui3DDiffuseLighting();
        modifyModelForPlayer(entityIn);
        model.setAngles(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_);
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        RenderSystem.matrixMode(5888);
        RenderSystem.enableLighting();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
    }

    private void modifyModelForPlayer(AbstractClientPlayerEntity entityIn) {
        model.isSneaking = entityIn.isCrouching();
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        ItemStack itemstack1 = entityIn.getHeldItemOffhand();
        BipedModel.ArmPose bipedmodel$armpose = this.getArmPose(entityIn, itemstack, itemstack1, Hand.MAIN_HAND);
        BipedModel.ArmPose bipedmodel$armpose1 = this.getArmPose(entityIn, itemstack, itemstack1, Hand.OFF_HAND);
        if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
            model.rightArmPose = bipedmodel$armpose;
            model.leftArmPose = bipedmodel$armpose1;
        } else {
            model.rightArmPose = bipedmodel$armpose1;
            model.leftArmPose = bipedmodel$armpose;
        }
    }


    private BipedModel.ArmPose getArmPose(AbstractClientPlayerEntity p_217766_1_, ItemStack p_217766_2_, ItemStack p_217766_3_, Hand p_217766_4_) {
        BipedModel.ArmPose bipedmodel$armpose = BipedModel.ArmPose.EMPTY;
        ItemStack itemstack = p_217766_4_ == Hand.MAIN_HAND ? p_217766_2_ : p_217766_3_;
        if (!itemstack.isEmpty()) {
            bipedmodel$armpose = BipedModel.ArmPose.ITEM;
            if (p_217766_1_.getItemInUseCount() > 0) {
                UseAction useaction = itemstack.getUseAction();
                if (useaction == UseAction.BLOCK) {
                    bipedmodel$armpose = BipedModel.ArmPose.BLOCK;
                } else if (useaction == UseAction.BOW) {
                    bipedmodel$armpose = BipedModel.ArmPose.BOW_AND_ARROW;
                } else if (useaction == UseAction.SPEAR) {
                    bipedmodel$armpose = BipedModel.ArmPose.THROW_SPEAR;
                } else if (useaction == UseAction.CROSSBOW && p_217766_4_ == p_217766_1_.getActiveHand()) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean flag3 = p_217766_2_.getItem() == Items.CROSSBOW;
                boolean flag = CrossbowItem.isCharged(p_217766_2_);
                boolean flag1 = p_217766_3_.getItem() == Items.CROSSBOW;
                boolean flag2 = CrossbowItem.isCharged(p_217766_3_);
                if (flag3 && flag) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }

                if (flag1 && flag2 && p_217766_2_.getItem().getUseAction(p_217766_2_) == UseAction.NONE) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }

        return bipedmodel$armpose;
    }
}
