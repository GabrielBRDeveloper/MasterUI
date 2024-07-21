package br.nullexcept.mux.res;

import br.nullexcept.mux.graphics.Size;

public interface DisplayInformation {
    Size getPhysicalSize();
    Size getResolution();
    String getMonitorId();
    String getName();
    float getMonitorScale();
}
