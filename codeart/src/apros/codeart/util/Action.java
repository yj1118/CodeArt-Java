package apros.codeart.util;

@FunctionalInterface
public interface Action {
	void apply() throws Exception;
}
