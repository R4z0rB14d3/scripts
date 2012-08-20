package scripts.state;

public class Constant<T> implements Value<T> {
	T value;
	public Constant(T value_) {
		value = value_;
	}
	public T get() { return value; }
	public void set(T newval) { value = newval; }
}
