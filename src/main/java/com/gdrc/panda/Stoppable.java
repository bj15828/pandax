package com.gdrc.panda;

/**
 * This interface is used by objects that create helper threads within their implementation.
 * The user of Stoppable components can start and stop these internal worker threads by called
 * start() and stop() respectively. The methods start() and stop() are not thread-safe and should
 * only be called in exactly the correct sequence - start(), then stop(), then start(), etc.
 */
public interface Stoppable {
    /**
     * Start all threads needed by the object.
     *
     * @throw Exception Is thrown when some thread cannot be properly started. In this case, the state
     * of the instance is not known. To reuse the instance, call stop() to reset the state of the instance.
     */
    public void start() throws PandaException;

    /**
     * Stops all threads in the object and release all resources. After this call, the object should be discarded.
     * Returns true if no errors were detected during the stop operaion. Returns false if an error was detected.
     * @return true if no errors were detected.
     */
    public boolean stop() throws PandaException;
}