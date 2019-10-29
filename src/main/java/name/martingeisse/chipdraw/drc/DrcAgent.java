package name.martingeisse.chipdraw.drc;

import name.martingeisse.chipdraw.Design;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Performs the DRC for a single window (possibly changing designs), or in general, one design at a time.
 *
 * This class may employ one or more background threads but offers an internally synchronized, single-threaded
 * interface. Performance-wise it should be noted that triggering a DRC may have to commit changes to the design made
 * by the caller thread so that the background threads can see them.
 */
public final class DrcAgent {

    private final AtomicReference<Design> trigger;
    private Design design;

    public DrcAgent() {
        this.trigger = new AtomicReference<>(null);
        this.design = null;
        new Thread(this::backgroundMain).start();
    }

    public void setDesign(Design design) {
        trigger.set(design);
    }

    public void trigger() {
        trigger.set(design);
    }

    private void backgroundMain() {
        try {
            while (true) { // TODO stop thread when closing the window
                waitForTrigger();
                Thread.sleep(3_000); // TODO simulates work to be done
                System.out.println("DRC finished");
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

}
