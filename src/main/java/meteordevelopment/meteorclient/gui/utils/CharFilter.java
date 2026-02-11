package meteordevelopment.meteorclient.gui.utils;

@FunctionalInterface
public interface CharFilter {
    boolean filter(String text, char c);
}
