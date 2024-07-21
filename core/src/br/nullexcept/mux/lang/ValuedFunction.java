package br.nullexcept.mux.lang;

public interface ValuedFunction<Input, Output> {
    Output run(Input value);
}
