package q.andres.support.utils.presets;

import q.andres.support.managers.SupportManager;

public class ObjectsPreset {
    public static SupportManager supportObject;

    public static void setObjects() {
        supportObject = new SupportManager();
        supportObject.set();
    }
}
