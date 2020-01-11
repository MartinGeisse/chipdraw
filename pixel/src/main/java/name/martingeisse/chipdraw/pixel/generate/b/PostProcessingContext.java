package name.martingeisse.chipdraw.pixel.generate.b;

public interface PostProcessingContext {

    //
    // one-sided connections to a specific target point
    //

    void connectPmosGate(int globalGateIndex, int targetX, int targetY);

    void connectNmosGate(int globalGateIndex, int targetX, int targetY);

    //
    // two-sided connections to a specific joint point
    //

    default void directConnectGates(int globalGateIndex, int jointX, int jointY) {
        crossConnectGates(globalGateIndex, globalGateIndex, jointX, jointY);
    }

    default void crossConnectGates(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int jointX, int jointY) {
        connectPmosGate(pmosGlobalGateIndex, jointX, jointY);
        connectNmosGate(nmosGlobalGateIndex, jointX, jointY);
    }

    //
    // two-sided connections to a 4x4 metal plate
    //

    default void directConnectGatesWithMetal(int globalGateIndex, int metalX, int metalY) {
        crossConnectGatesWithMetal(globalGateIndex, globalGateIndex, metalX, metalY);
    }

    default void directConnectGatesWithMetal(int globalGateIndex, int metalX, int metalY,
                                                    int polyPadDisplacementX, int polyPadDisplacementY) {
        crossConnectGatesWithMetal(globalGateIndex, globalGateIndex, metalX, metalY,
                polyPadDisplacementX, polyPadDisplacementY);
    }

    default void crossConnectGatesWithMetal(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int metalX, int metalY) {
        crossConnectGatesWithMetal(pmosGlobalGateIndex, nmosGlobalGateIndex, metalX, metalY, 0, 0);
    }

    void crossConnectGatesWithMetal(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int metalX, int metalY,
                                           int polyPadDisplacementX, int polyPadDisplacementY);

    //
    // two-sided connections to a cell port (== tile-aligned 4x4 metal plate)
    //

    default void directConnectGatesWithPort(int globalGateIndex, int portTileX, int portTileY) {
        crossConnectGatesWithPort(globalGateIndex, globalGateIndex, portTileX, portTileY);
    }

    default void directConnectGatesWithPort(int globalGateIndex, int portTileX, int portTileY,
                                            int polyPadDisplacementX, int polyPadDisplacementY) {
        crossConnectGatesWithPort(globalGateIndex, globalGateIndex, portTileX, portTileY,
                polyPadDisplacementX, polyPadDisplacementY);
    }

    default void crossConnectGatesWithPort(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int portTileX, int portTileY) {
        crossConnectGatesWithPort(pmosGlobalGateIndex, nmosGlobalGateIndex, portTileX, portTileY, 0, 0);
    }

    void crossConnectGatesWithPort(int pmosGlobalGateIndex, int nmosGlobalGateIndex, int portTileX, int portTileY,
                                   int polyPadDisplacementX, int polyPadDisplacementY);

}
