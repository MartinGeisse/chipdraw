package name.martingeisse.chipdraw.pixel.generate.b;

public interface PostProcessingContext {

    void connectPmosGate(int globalGateIndex, int targetX, int targetY);

    void connectNmosGate(int globalGateIndex, int targetX, int targetY);

    default void directConnectGates(int globalGateIndex, int jointX, int jointY) {
        crossConnectGates(globalGateIndex, globalGateIndex, jointX, jointY);
    }

    default void crossConnectGates(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int jointX, int jointY) {
        connectPmosGate(pmosGlobalGateIndex, jointX, jointY);
        connectNmosGate(nmosGlobalGateIndex, jointX, jointY);
    }

    default void directConnectGatesWithMetalContact(int globalGateIndex, int contactX, int contactY) {
        crossConnectGatesWithMetalContact(globalGateIndex, globalGateIndex, contactX, contactY);
    }

    void crossConnectGatesWithMetalContact(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int contactX, int contactY);

    default void directConnectGatesWithPort(int globalGateIndex, int portTileX, int portTileY) {
        crossConnectGatesWithPort(globalGateIndex, globalGateIndex, portTileX, portTileY);
    }

    void crossConnectGatesWithPort(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int portTileX, int portTileY);

}
