package io.github.imurx.localizedbrowser.mixin.jei;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.core.search.PrefixInfo;
import mezz.jei.gui.ingredients.IListElementInfo;
import mezz.jei.gui.search.ElementPrefixParser;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.function.Supplier;

@Pseudo
@Mixin(ElementPrefixParser.class)
public class MixinElementPrefixParser {
    @Dynamic
    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "mezz/jei/core/search/PrefixInfo"))
    private static <T, I> PrefixInfo onNoPrefixInit(char prefix, PrefixInfo.IModeGetter modeGetter, PrefixInfo.IStringsGetter<T> stringsGetter, Supplier storageSupplier, Operation<PrefixInfo<T, I>> original) {
        return new PrefixInfo<T, I>(prefix, modeGetter,
                (t) -> stringsGetter
                        .getStrings(t)
                        .stream()
                        .flatMap(s -> LocalizedBrowser.getInstance().parseOutputs(s).stream())
                        .toList()
                , storageSupplier);
    }

    @Dynamic
    @WrapMethod(method = "lambda$new$3")
    private static Collection<String> onTooltipStringPrefix(IIngredientFilterConfig config, IIngredientManager ingredientManager, IListElementInfo e, Operation<Collection<String>> original) {
        return original.call(config, ingredientManager, e)
                .stream()
                .flatMap(s -> LocalizedBrowser.getInstance().parseOutputs(s).stream())
                .toList();
    }
}
