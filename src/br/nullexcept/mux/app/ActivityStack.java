package br.nullexcept.mux.app;

class ActivityStack {
    private Activity activity;
    private final ActivityStack back;

    ActivityStack(Activity activity, ActivityStack back) {
        this.activity = activity;
        this.back = back;
    }

    ActivityStack(Activity activity) {
        this(activity, null);
    }

    public ActivityStack newStack(Activity ctx) {
        return new ActivityStack(ctx, this);
    }

    public ActivityStack getBackItem() {
        ActivityStack back = this.back;
        while (back != null && !back.isValid()) {
            back = back.getBackItem();
        }
        if (back != null && !back.isValid()) {
            back = null;
        }
        return back;
    }

    public void invalidate() {
        activity = null;
    }

    public boolean isValid() {
        return activity != null;
    }

    public Activity getActivity() {
        return activity;
    }
}
