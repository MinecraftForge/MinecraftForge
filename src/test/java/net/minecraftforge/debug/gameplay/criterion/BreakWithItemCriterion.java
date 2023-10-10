package net.minecraftforge.debug.gameplay.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BreakWithItemCriterion extends SimpleCriterionTrigger<BreakWithItemCriterion.Instance> {
	// this will be removed once Mojang figure out how to decode context aware predicates with codecs
	private static final ThreadLocal<ArrayDeque<Optional<ContextAwarePredicate>>> stack = ThreadLocal.withInitial(ArrayDeque::new);

	private static final class ContextPredicate extends MapCodec<Optional<ContextAwarePredicate>> {
		public static final ContextPredicate INSTANCE = new ContextPredicate();

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.of(ops.createString("player"));
		}

		@Override
		public <T> DataResult<Optional<ContextAwarePredicate>> decode(DynamicOps<T> ops, MapLike<T> input) {
			if (stack.get().isEmpty())
				return DataResult.error(() -> "Not deserializing criterion");
			else
				return DataResult.success(stack.get().getLast());
		}

		@Override
		public <T> RecordBuilder<T> encode(Optional<ContextAwarePredicate> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			if (input.isPresent())
				return prefix.add("player", JsonOps.INSTANCE.convertTo(ops, input.get().toJson()));

			return prefix;
		}
	}

	private static final Codec<Instance> CODEC = RecordCodecBuilder.create(it -> {
		return it.group(
			ContextPredicate.INSTANCE.forGetter(AbstractCriterionTriggerInstance::playerPredicate),
			BlockPredicate.CODEC.fieldOf("breakingBlock").forGetter(i -> i.breakingBlock),
			ItemPredicate.CODEC.fieldOf("holdingItem").forGetter(i -> i.holdingItem),
			Codec.BOOL.optionalFieldOf("allowOffhand", false).forGetter(i -> i.allowOffhand)
		).apply(it, Instance::new);
	});

	@Override
	protected Instance createInstance(JsonObject data, Optional<ContextAwarePredicate> playerPredicate, DeserializationContext context) {
		try {
			stack.get().addLast(playerPredicate);

			return CODEC.decode(JsonOps.INSTANCE, data)
				.getOrThrow(false, e -> {
					throw new JsonSyntaxException("Unable to deserialize criterion instance: " + e);
				})
				.getFirst();
		} finally {
			stack.get().removeLast();
		}
	}

	public void trigger(ServerPlayer player, BlockPos block) {
		this.trigger(player, it ->
			it.breakingBlock.matches((ServerLevel) player.level(), block) && (
				it.holdingItem.matches(player.getItemInHand(InteractionHand.MAIN_HAND)) ||
					it.allowOffhand && it.holdingItem.matches(player.getItemInHand(InteractionHand.OFF_HAND))
			));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static final class Instance extends AbstractCriterionTriggerInstance {
		public final BlockPredicate breakingBlock;
		public final ItemPredicate holdingItem;
		public final boolean allowOffhand;

		public Instance(Optional<ContextAwarePredicate> playerPredicate, BlockPredicate breakingBlock, ItemPredicate holdingItem, boolean allowOffhand) {
			super(playerPredicate);
			this.breakingBlock = breakingBlock;
			this.holdingItem = holdingItem;
			this.allowOffhand = allowOffhand;
		}

		public Instance(BlockPredicate breakingBlock, ItemPredicate holdingItem) {
			this(Optional.empty(), breakingBlock, holdingItem, false);
		}

		@Override
		public JsonObject serializeToJson() {
			return (JsonObject) CODEC
				.encodeStart(JsonOps.INSTANCE, this)
				.getOrThrow(false, e -> {
					throw new JsonSyntaxException("Unable to serialize criterion instance: " + e);
				});
		}
	}
}
