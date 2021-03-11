package team.catgirl.collar.mod.forge.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;

public class VoidArgumentType implements ArgumentType<Void> {

    public VoidArgumentType() {}

    @Override
    public Void parse(StringReader reader) {
        return null;
    }

    public static VoidArgumentType none() {
        return new VoidArgumentType();
    }
}
