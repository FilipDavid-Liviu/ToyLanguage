package model.statements.semaphore;

import model.ProgramState;
import model.adt.Pair;
import model.dt.ISemaphoreTable;
import model.dt.TypeDictionary;
import model.exceptions.SemaphoreException;
import model.statements.Statement;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReleaseStatement implements Statement {
    private final String id;
    private static final Lock lock = new ReentrantLock();

    public ReleaseStatement(String id) {
        this.id = id;
    }

    @Override
    public ProgramState execute(ProgramState state) {
        lock.lock();
        ISemaphoreTable semaphoreTable = state.getSemaphoreTable();
        if (!semaphoreTable.isDefined(this.id))
            throw new SemaphoreException(2, this.id);
        Pair<Integer, List<Integer>> pair = semaphoreTable.lookUp(this.id);
        List<Integer> list = pair.getSecond();
        if (!list.contains(state.getId())) {
            lock.unlock();
            throw new SemaphoreException(2, this.id, state.getId());
        }
        else semaphoreTable.release(this.id, state.getId());
        lock.unlock();
        return null;
    }

    @Override
    public Statement deepCopy() {
        return new ReleaseStatement(this.id);
    }

    @Override
    public String toString() {
        return "release(" + this.id + ")";
    }

    @Override
    public TypeDictionary typeCheck(TypeDictionary typeDictionary) {
        return typeDictionary;
    }
}
