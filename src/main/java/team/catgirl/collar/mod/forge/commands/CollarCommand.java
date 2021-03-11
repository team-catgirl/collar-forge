package team.catgirl.collar.mod.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import team.catgirl.collar.mod.service.CollarService;

public class CollarCommand extends CollarCommandBase {

    private final CollarService collarService;

    public CollarCommand(CollarService collarService) {
        super("collar", collarService);
        this.collarService = collarService;
    }

    @Override
    protected void registerAll(CommandDispatcher<CollarService> dispatcher) {
        dispatcher.register(literal("connect").executes(context -> {
            collarService.connect();
            return 1;
        }));
        dispatcher.register(literal("disconnect").executes(context -> {
            collarService.disconnect();
            return 1;
        }));
    }
}
