package xyz.bluspring.hummingbirdorigin

import io.github.apace100.apoli.power.Power
import io.github.apace100.apoli.power.PowerType
import io.github.apace100.apoli.power.PowerTypeReference
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation

class HummingbirdOrigin : ModInitializer {
    override fun onInitialize() {
    }

    companion object {
        val floralConsumption: PowerType<*> = PowerTypeReference<Power>(ResourceLocation("hummingbird", "floral_consumption"))
    }
}