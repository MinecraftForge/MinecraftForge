package net.minecraftforge.common.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import javax.annotation.Nullable;

/**
 * Interface used in order to determine if a {@link ExistingFileHelper} should validate the existence of a path.
 */
@FunctionalInterface
public interface ValidationPredicate
{
    ValidationPredicate TRUE = (type, path, pack) -> true;

    ResourceLocation MODELS_VALIDATION_TYPE = new ResourceLocation("forge", "models");
    ResourceLocation TAGS_VALIDATION_TYPE = new ResourceLocation("forge", "tags");

    /**
     * {@return if the path should be validated}
     *
     * @param validationType the type of the validation
     * @param requestedPath  the path that should be validated
     * @param packType       the type of the resource which should be validated
     */
    boolean canValidate(@Nullable ResourceLocation validationType, ResourceLocation requestedPath, PackType packType);

    default ValidationPredicate and(ValidationPredicate other)
    {
        return (validationType, requestedPath, packType) -> canValidate(validationType, requestedPath, packType) && other.canValidate(validationType, requestedPath, packType);
    }

    default ValidationPredicate or(ValidationPredicate other)
    {
        return (validationType, requestedPath, packType) -> canValidate(validationType, requestedPath, packType) || other.canValidate(validationType, requestedPath, packType);
    }

    default ValidationPredicate not()
    {
        return (validationType, requestedPath, packType) -> !this.canValidate(validationType, requestedPath, packType);
    }
}
