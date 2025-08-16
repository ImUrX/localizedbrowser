package io.github.imurx.localizedbrowser.mixin.controlling;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "com.blamejared.searchables.api.SearchableType")
public class MixinSearchableType<T> {
    // TODO: Make somehow Controlling have more than one string being compared for filtering
//    @WrapOperation(
//            method = "filterEntries(Ljava/util/List;Ljava/lang/String;Ljava/util/function/Predicate;)Ljava/util/List;",
//            at = @At(value = "INVOKE", target = "Ljava/util/Optional;map(Ljava/util/function/Function;)Ljava/util/Optional;")
//    )
//    private Optional<Predicate<T>> onFilterEntries(Optional instance, Function<? super T, ? extends Predicate<T>> mapper, Operation<Optional<Predicate<T>>> original) {
//        return original.call(instance, mapper).map(p -> p.or((a) -> {
//            if(a.getClass() != ControlsListWidget.KeyBindingEntry.class) return false;
//            for (String s : LocalizedBrowser.getInstance().parseOutputs(((ControlsListWidget.KeyBindingEntry) a).)) {
//                if(p.test((T) s)) return true;
//            }
//            return false;
//        }));
//    }
}
