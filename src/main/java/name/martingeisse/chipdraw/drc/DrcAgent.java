package name.martingeisse.chipdraw.drc;

import com.google.common.collect.ImmutableList;
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
    private final AtomicReference<ImmutableList<Violation>> result;
    private Design design;

    public DrcAgent() {
        this.trigger = new AtomicReference<>(null);
        this.result = new AtomicReference<>(null);
        this.design = null;
        new Thread(this::backgroundMain).start();
    }

    public void setDesign(Design design) {
        trigger.set(design);
        result.set(null);
    }

    public void trigger() {
        trigger.set(design);
        result.set(null);
    }

    private void backgroundMain() {
        try {
            while (true) { // TODO stop thread when closing the window
                waitForTrigger();
                DrcContext context = new DrcContext(design);
                new Drc().perform(context);
                ImmutableList<Violation> violations = context.getViolations();
                for (Violation violation : violations) {
                    System.out.println("*** " + violation.getFullText());
                }
                result.set(violations);
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
