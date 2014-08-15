package de.tum.in.i22.uc.blocks.workspace;

public interface ISupportMemento {

    public Object getState();

    public void loadState(Object memento);
}
