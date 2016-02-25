package rationale.emotions.threads;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HookExplorer {
    private HookExplorer() {
        //Utility class. Hide the constructor
    }

    public static List<Thread> getShutdownHooks() {
        List<Thread> allHooks = new ArrayList<>();
        try {
            Map<Thread, Thread> shutdownHooks = getHooks();
            return new ArrayList<>(shutdownHooks.keySet());
        } catch (Exception e) {

        }
        return allHooks;
    }

    private static Map<Thread, Thread> getHooks() throws Exception {
        Class clazz = Class.forName("java.lang.ApplicationShutdownHooks");
        Field hooks = clazz.getDeclaredField("hooks");
        hooks.setAccessible(true);
        return (Map<Thread, Thread>) hooks.get(null);
    }

    public static List<ThreadInfo> getShutdownHooksInfo() {
        List<Thread> threads = getShutdownHooks();
        List<ThreadInfo> allInfo = new ArrayList<>();
        for (Thread thread : threads) {
            ThreadInfo info = new ThreadInfo();
            info.setPriority(thread.getPriority());
            info.setThreadGroupName(thread.getThreadGroup().getName());
            info.setThreadId(thread.getId());
            info.setThreadName(thread.getName());
            if (thread.getContextClassLoader() != null) {
                info.setClassLoader(thread.getContextClassLoader().toString());
            }
            if (thread.getClass() != null) {
                info.setClassName(thread.getClass().getName());
            }
            allInfo.add(info);
        }
        return allInfo;
    }

}
