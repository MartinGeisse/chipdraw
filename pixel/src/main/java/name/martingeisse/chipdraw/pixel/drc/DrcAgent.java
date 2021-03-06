package name.martingeisse.chipdraw.pixel.drc;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.Design;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Performs the DRC for a single window (possibly changing designs), or in general, one design at a time.
 *
 * This class may employ one or more background threads but offers an internally synchronized, single-threaded
 * interface. Performance-wise it should be noted that triggering a DRC may have to commit changes to the design made
 * by the caller thread so that the background threads can see them.
 */
public final class DrcAgent {

    private final AtomicReference<Design> trigger;
    private final ConcurrentHashMap<Object, Consumer<ImmutableList<Violation>>> resultListeners;
    private final Thread thread;
    private Design design;
    private boolean stopped = false;

    public DrcAgent() {
        this.trigger = new AtomicReference<>(null);
        this.resultListeners = new ConcurrentHashMap<>();
        this.design = null;
        thread = new Thread(this::backgroundMain);
        thread.setDaemon(true);
        thread.start();
    }

    public void addResultListener(Consumer<ImmutableList<Violation>> listener) {
        resultListeners.put(listener, listener);
    }

    public void removeResultListener(Consumer<ImmutableList<Violation>> listener) {
        resultListeners.remove(listener);
    }

    public void setDesign(Design design) {
        trigger.set(design);
    }

    public void trigger() {
        trigger.set(design);
    }

    private void backgroundMain() {
        try {
            while (!stopped) {
                waitForTrigger();
                DrcContext context = new DrcContext(design);
                Drc drc = design.getTechnology().getBehavior().getDrc();
                drc.perform(context);
                ImmutableList<Violation> violations = context.getViolations();
                for (Consumer<ImmutableList<Violation>> resultListener : resultListeners.values()) {
                    resultListener.accept(violations);
                }
            }
        } catch (InterruptedException e) {
        }
    }

    // TODO causes the first microscopic change to run the DRC, then when finished again since the remaining changes have happened
    private void waitForTrigger() throws InterruptedException {
        while (true) {
            Design newDesign = trigger.getAndSet(null);
            if (newDesign != null) {
                this.design = newDesign;
                return;
            }
            Thread.sleep(100);
        }
    }

    public void dispose() {
        stopped = true;
        thread.interrupt();
    }

}
