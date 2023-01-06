package com.melon.project.serverless.lambda.proxy.context;

import com.amazonaws.services.lambda.runtime.Context;

public class StaticLambdaContext {
    private static Context context;

    public static void setContext(Context context) {
        StaticLambdaContext.context = context;
    }

    public static Context getContext() {
        return context;
    }
}
