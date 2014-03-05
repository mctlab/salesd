package com.mctlab.ansight.common.annotation;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.mctlab.ansight.common.util.ReflectUtils;

import java.lang.reflect.Field;
import java.util.List;

public class Injector {

    /**
     * inject view from activity
     */
    public static void inject(Object fieldOwner, Activity activity) {
        try {
            injectView(fieldOwner, activity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * inject view from dialog
     */
    public static void inject(Object fieldOwner, Dialog dialog) {
        try {
            injectView(fieldOwner, dialog);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * inject from view
     */
    public static void inject(Object fieldOwner, View view) {
        try {
            injectView(fieldOwner, view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectView(Object fieldOwner, Object viewProvider) throws Exception {
        List<Field> fields = ReflectUtils.getFields(fieldOwner.getClass());
        for (Field field : fields) {
            ViewId viewId = field.getAnnotation(ViewId.class);
            if (viewId == null) {
                continue;
            }
            Object view = null;
            if (viewProvider instanceof Activity) {
                view = ((Activity) viewProvider).findViewById(viewId.value());
            } else if (viewProvider instanceof View) {
                view = ((View) viewProvider).findViewById(viewId.value());
            } else if (viewProvider instanceof Dialog) {
                view = ((Dialog) viewProvider).findViewById(viewId.value());
            }
            if (view != null) {
                field.setAccessible(true);
                field.set(fieldOwner, view);
            }
        }
    }

}
