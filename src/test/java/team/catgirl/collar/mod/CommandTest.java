package team.catgirl.collar.mod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import team.catgirl.collar.mod.service.CollarService;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class CommandTest {

    public static void main(String[] args) throws CommandSyntaxException {
        CollarService service;

        CommandDispatcher<CommandTest> dispatcher = new CommandDispatcher<>();

//        dispatcher.register(LiteralArgumentBuilder.<CommandTest>literal("group")
//                .then(argument("name", string()))
//                .then(LiteralArgumentBuilder.<CommandTest>literal("add"))
//                .then(argument("user", string()))
//                .executes(context -> {
//                    System.out.println("hello");
//                    return 1;
//                }));


        dispatcher.register(LiteralArgumentBuilder.<CommandTest>literal("group")
            .then(RequiredArgumentBuilder.<CommandTest, String>argument("name", string())
                    .then(LiteralArgumentBuilder.<CommandTest>literal("add")
                        .then(RequiredArgumentBuilder.<CommandTest, String>argument("user", string())
                                .executes(context -> 1)
                        )
                    )
            )
        );

        try {
                int execute = dispatcher.execute("group cutegroup add orsond", new CommandTest());
            System.out.println(execute);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
