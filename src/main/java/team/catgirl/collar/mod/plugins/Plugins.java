package team.catgirl.collar.mod.plugins;

import team.catgirl.collar.api.CollarPlugin;

import java.util.stream.Stream;

public interface Plugins {
    Stream<CollarPlugin> find();
}
