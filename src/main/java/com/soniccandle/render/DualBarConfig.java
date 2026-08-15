package com.soniccandle.render;

public final class DualBarConfig {

    private final DualBarLayout layout;
    private final DualBarReach reach;
    private final boolean reverseTop;
    private final boolean reverseBottom;
    private final DualBarVisibility visibility;

    public DualBarConfig(DualBarLayout layout, DualBarReach reach,
            boolean reverseTop, boolean reverseBottom) {
        this(layout, reach, reverseTop, reverseBottom, DualBarVisibility.BOTH);
    }

    public DualBarConfig(DualBarLayout layout, DualBarReach reach,
            boolean reverseTop, boolean reverseBottom, DualBarVisibility visibility) {
        this.layout = layout == null ? DualBarLayout.EDGES : layout;
        this.reach = reach == null ? DualBarReach.MEDIUM : reach;
        this.reverseTop = reverseTop;
        this.reverseBottom = reverseBottom;
        this.visibility = visibility == null ? DualBarVisibility.BOTH : visibility;
    }

    public static DualBarConfig defaults() {
        return new DualBarConfig(DualBarLayout.EDGES, DualBarReach.MEDIUM, false, false);
    }

    public DualBarLayout layout() { return layout; }
    public DualBarReach reach() { return reach; }
    public boolean reverseTop() { return reverseTop; }
    public boolean reverseBottom() { return reverseBottom; }
    public DualBarVisibility visibility() { return visibility; }
}
