package com.lxy.rpc.spi;

import com.lxy.rpc.annotation.LXYSPI;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class ExtensionLoader<T> {
    //供spring适配使用
    public ExtensionLoader(){
        //spring bean的不该赋值
        type = null;
    }
    /**
     * 接口的名称与Extensionloader的对应表
     */
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap();

    /**
     * 具体类实例的class类型
     */
    private final Class<?> type;

//    /**
//     * 适配器内聚合的下辖子类
//     */
//    private ConcurrentLinkedDeque<Object> cachedAdaptiveInstance = new ConcurrentLinkedDeque<>();
//
//    /**
//     * 创建适配器时候异常
//     */
//    private volatile Throwable createAdaptiveInstanceError;

    /**
     * 接口的实现类的子类缓存
     */
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap();
//
//    /**
//     * AOP类集合
//     */
//    private Set<Class<?>> cachedWrapperClasses;

    /**
     * 接口的全部相关类|实现类
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder();

    /**
     * 接口实例和实现类名 缓存映射表
     */
    ConcurrentHashMap<String,Holder> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 我愿称之为容器
     * @param <T>
     */
    class Holder<T>{
        private T instance;

        public T get() {
            return instance;
        }
        public void set(T instance){
            this.instance = instance;
        }
    }

    public ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    public T getExtension(String name) {
        if (name != null && name.length() != 0) {
            if ("true".equals(name)) {
                return this.getDefaultExtension();
            } else {
                Holder<Object> holder = (Holder)this.cachedInstances.get(name);
                if (holder == null) {
                    holder = new Holder<>();
                    this.cachedInstances.putIfAbsent(name, holder);
                    //没有的话就创建一个
                }
                //并初始化
                Object instance = holder.get();
                if (instance == null) {
                    synchronized(holder) {
                        instance = holder.get();
                        if (instance == null) {
                            //创建
                            instance = this.createExtension(name);
                            //给初始化的项目赋值
                            holder.set(instance);
                        }
                    }
                }
                return (T) instance;
            }
        } else {
            throw new IllegalArgumentException("Extension name == null");
        }
    }


    private T createExtension(String name) {
        /**
         * 要反射获取，需要先获取class类，要获取class类需要先读取配置文件，并格式化到内存，下面方法
         * （一次读取会将读取的信息（类）缓存起来（一次加载全部接口的类，不会加载无关类，根据接口名称来选择的文件））
         *
         * 启发：大神写的代码也没有实现配置文件的拓展：依托配置文件，一次性加载，不可热配置
         * 但为什么还值得学习：
         * 需求决定：
         * 1，dubbo spi 的核心就是按需加载，加载相关接口的相关内容（因此不使用原生spi）。
         * 2，并提供拓展点：客户自定义组件，便于项目发展。
         * 3，aop，ioc
         * 大神依然完成需要，启发我们些项目有时候是不能面面俱到的，适合是最好的
         */
        Map<String, Class<?>> extensionClasses = this.getExtensionClasses();
        Class<?> clazz = extensionClasses.get(name);
        if (clazz == null) {
            throw new RuntimeException("无法找到目标类"+name);
        } else {
            try {
                T instance = (T) EXTENSION_INSTANCES.get(clazz);
                if (instance == null) {
                    EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                    instance = (T)EXTENSION_INSTANCES.get(clazz);
                }

                this.injectExtension(instance);
                /**
                 * AOP链
                 */
//                Set<Class<?>> wrapperClasses = this.cachedWrapperClasses;
//                Class wrapperClass;
//                //AOP
//                if (wrapperClasses != null && wrapperClasses.size() > 0) {
//                    for(Iterator i$ = wrapperClasses.iterator(); i$.hasNext(); instance = this.injectExtension(wrapperClass.getConstructor(this.type).newInstance(instance))) {
//                        wrapperClass = (Class)i$.next();
//                    }
//                }

                return instance;
            } catch (Throwable var7) {
                throw new IllegalStateException("Extension instance(name: " + name + ", class: " + this.type + ")  could not be instantiated: " + var7.getMessage(), var7);
            }
        }
    }
    public void injectExtension(T instance){

    }

    private String cachedDefaultName;

    public T getDefaultExtension() {
        this.getExtensionClasses();
        return null != this.cachedDefaultName
                && this.cachedDefaultName.length() != 0
                && !"true".equals(this.cachedDefaultName)
                ? this.getExtension(this.cachedDefaultName) : null;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        //先走缓存
        Map<String, Class<?>> classes = (Map)this.cachedClasses.get();

        if (classes == null) {
            synchronized(this.cachedClasses) {
                classes = (Map)this.cachedClasses.get();
                if (classes == null) {
                    //加载文件，并载入缓存
                    /**
                     * 按需class文件，按需加载class类，一次加载一类，而不是把全部目录的全部加载到内存
                     * 缓存表为cachedClasses
                     */
                    classes = this.loadExtensionClasses();
                    /**
                     * 内部包含了目标注解的全部类：如接口Log，那么这里面包含全部的Log相关的类
                     * 建为类名（简单类名），值为 类
                     * 是实例方法，因此每个接口的内容不同，比如这里，Log，其实现的类都是自己的
                     * 其他接口的内容都是其他的
                     */
                    this.cachedClasses.set(classes);
                }
            }
        }

        return classes;
    }

    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");

    private Map<String, Class<?>> loadExtensionClasses() {
        LXYSPI defaultAnnotation = (LXYSPI)this.type.getAnnotation(LXYSPI.class);
        if (defaultAnnotation != null) {
            String value = defaultAnnotation.value();
            if (value != null && (value = value.trim()).length() > 0) {
                String[] names = NAME_SEPARATOR.split(value);
                if (names.length > 1) {
                    throw new IllegalStateException("more than 1 default extension name on extension " + this.type.getName() + ": " + Arrays.toString(names));
                }

                if (names.length == 1) {
                    this.cachedDefaultName = names[0];
                }
            }
        }

        Map<String, Class<?>> extensionClasses = new HashMap();
        this.loadFile(extensionClasses, "META-INF/lxy/internal/");
        this.loadFile(extensionClasses, "META-INF/lxy/");
        this.loadFile(extensionClasses, "META-INF/services/");
        return extensionClasses;
    }
    private static ClassLoader findClassLoader() {
        return ExtensionLoader.class.getClassLoader();
    }
//    private void loadFile(Map<String, Class<?>> extensionClasses, String s) {
//    }

    private void loadFile(Map<String, Class<?>> extensionClasses, String dir) {
        String fileName = dir + this.type.getName();

        try {
            ClassLoader classLoader = findClassLoader();
            Enumeration urls;
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }

            if (urls != null) {
                label269:
                while(urls.hasMoreElements()) {
                    java.net.URL url = (java.net.URL)urls.nextElement();

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));

                        try {
                            String line = null;

                            while(true) {
                                do {
                                    if ((line = reader.readLine()) == null) {
                                        continue label269;
                                    }

                                    int ci = line.indexOf(35);
                                    if (ci >= 0) {
                                        line = line.substring(0, ci);
                                    }

                                    line = line.trim();
                                } while (line.length() <= 0);

                                try {
                                    String name = null;
                                    /**
                                     * 哈哈哈这里不讲人话，61的码值即为’=‘，这里是使用分隔符，获取名称和全限定类名，接下来就要装载了
                                     */
                                    int i = line.indexOf(61);
                                    if (i > 0) {
                                        name = line.substring(0, i).trim();
                                        line = line.substring(i + 1).trim();
                                    }

                                    if (line.length() > 0) {

                                        Class<?> clazz = Class.forName(line, true, classLoader);
                                        if (!this.type.isAssignableFrom(clazz)) {
                                            throw new IllegalStateException("Error when load extension class(interface: " + this.type + ", class line: " + clazz.getName() + "), class " + clazz.getName() + "is not subtype of interface.");
                                        }


/**
 * 适配器代码
 */
//                                    if (clazz.isAnnotationPresent(Adaptive.class)) {
//                                        if (this.cachedAdaptiveClass == null) {
//                                            this.cachedAdaptiveClass = clazz;
//                                        } else if (!this.cachedAdaptiveClass.equals(clazz)) {
//                                            throw new IllegalStateException("More than 1 adaptive class found: " + this.cachedAdaptiveClass.getClass().getName() + ", " + clazz.getClass().getName());
//                                        }
//                                    }
                                        /**
                                         * AOP代码
                                         */
//                                    else {
//                                        try {
//                                            clazz.getConstructor(this.type);
//                                            Set<Class<?>> wrappers = this.cachedWrapperClasses;
//                                            if (wrappers == null) {
//                                                this.cachedWrapperClasses = new ConcurrentHashSet();
//                                                wrappers = this.cachedWrapperClasses;
//                                            }
//
//                                            wrappers.add(clazz);

                                        /**
                                         * 空参构造：目标实现类代码
                                         */
//                                        } catch (NoSuchMethodException var27) {
//                                            clazz.getConstructor();
//                                            if (name == null || name.length() == 0) {
//                                                name = this.findAnnotationName(clazz);
//                                                if (name == null || name.length() == 0) {
//                                                    if (clazz.getSimpleName().length() <= this.type.getSimpleName().length() || !clazz.getSimpleName().endsWith(this.type.getSimpleName())) {
//                                                        throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + url);
//                                                    }
//
//                                                    name = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - this.type.getSimpleName().length()).toLowerCase();
//                                                }
//                                            }
//
//                                            String[] names = NAME_SEPARATOR.split(name);
//                                            if (names != null && names.length > 0) {
//                                                Activate activate = (Activate)clazz.getAnnotation(Activate.class);
//                                                if (activate != null) {
//                                                    this.cachedActivates.put(names[0], activate);
//                                                }
//
//                                                String[] arr$ = names;
//                                                int len$ = names.length;
//
//                                                for(int i$ = 0; i$ < len$; ++i$) {
//                                                    String n = arr$[i$];
//                                                    if (!this.cachedNames.containsKey(clazz)) {
//                                                        this.cachedNames.put(clazz, n);
//                                                    }
//
//                                                    Class<?> c = (Class)extensionClasses.get(n);
//                                                    if (c == null) {
//                                                        extensionClasses.put(n, clazz);
//                                                    } else if (c != clazz) {
//                                                        throw new IllegalStateException("Duplicate extension " + this.type.getName() + " name " + n + " on " + c.getName() + " and " + clazz.getName());
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }

                                        clazz.getConstructor();
                                        if (name == null || name.length() == 0) {
                                            name = this.findAnnotationName(clazz);
                                            if (name == null || name.length() == 0) {
                                                /**
                                                 * 注解内的目标的名称要与目标接口相同
                                                 */
                                                if (clazz.getSimpleName().length() <= this.type.getSimpleName().length() || !clazz.getSimpleName().endsWith(this.type.getSimpleName())) {
                                                    throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + url);
                                                }
                                                name = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - this.type.getSimpleName().length()).toLowerCase();
                                            }
                                        }

                                        /**
                                         * 放入目标缓存
                                         */
                                        extensionClasses.put(name, clazz);


                                        /**
                                         * 解析适配器代码
                                         */
//                                        String[] names = NAME_SEPARATOR.split(name);
//                                        if (names != null && names.length > 0) {
//                                            Activate activate = (Activate) clazz.getAnnotation(Activate.class);
//                                            if (activate != null) {
//                                                this.cachedActivates.put(names[0], activate);
//                                            }
//
//                                            String[] arr$ = names;
//                                            int len$ = names.length;
//
//                                            for (int i$ = 0; i$ < len$; ++i$) {
//                                                String n = arr$[i$];
//                                                if (!this.cachedNames.containsKey(clazz)) {
//                                                    this.cachedNames.put(clazz, n);
//                                                }
//
//                                                Class<?> c = (Class) extensionClasses.get(n);
//                                                if (c == null) {
//                                                    extensionClasses.put(n, clazz);
//                                                } else if (c != clazz) {
//                                                    throw new IllegalStateException("Duplicate extension " + this.type.getName() + " name " + n + " on " + c.getName() + " and " + clazz.getName());
//                                                }
//                                            }
//                                        }
                                    }
                                } catch (Throwable var28) {
                                    IllegalStateException e = new IllegalStateException("Failed to load extension class(interface: " + this.type + ", class line: " + line + ") in " + url + ", cause: " + var28.getMessage(), var28);
                                    e.printStackTrace();
//                                this.exceptions.put(line, e);
                                }
                            }
                        } finally {
                            reader.close();
                        }
                    } catch (Throwable var30) {
                    }
                }
            }
        } catch (Throwable var31) {
        }

    }


    private String findAnnotationName(Class<?> clazz) {
        return null;
//        Extension extension = (Extension)clazz.getAnnotation(Extension.class);
//        if (extension == null) {
//            String name = clazz.getSimpleName();
//            if (name.endsWith(this.type.getSimpleName())) {
//                name = name.substring(0, name.length() - this.type.getSimpleName().length());
//            }
//
//            return name.toLowerCase();
//        } else {
//            return extension.value();
//        }
    }

//    private final ExtensionFactory objectFactory;
//
//    public ExtensionLoader(Class<T> type) {
//        this.type = type;
//        this.objectFactory = type == ExtensionFactory.class ? null : (ExtensionFactory)getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension();
//
//    }
//
//    /**
//     * 创建适配器，用于多配置，请求动态配置组件
//     * @return
//     */
//    public T getAdaptiveExtension() {
//        Object instance = this.cachedAdaptiveInstance;
//        if (instance == null) {
//            if (this.createAdaptiveInstanceError != null) {
//                throw new IllegalStateException("fail to create adaptive instance: " + this.createAdaptiveInstanceError.toString(), this.createAdaptiveInstanceError);
//            }
//
//            synchronized(this.cachedAdaptiveInstance) {
//                instance = this.cachedAdaptiveInstance;
//                if (instance == null) {
//                    try {
//                        instance = this.createAdaptiveExtension();
//                        final ConcurrentLinkedDeque<Object> instances = new ConcurrentLinkedDeque<>();
//                        instances.add(instance);
//                        this.cachedAdaptiveInstance = instances;
//                    } catch (Throwable var5) {
//                        this.createAdaptiveInstanceError = var5;
//                        throw new IllegalStateException("fail to create adaptive instance: " + var5.toString(), var5);
//                    }
//                }
//            }
//        }
//        return (T) instance;
//    }
//
//    private T createAdaptiveExtension() {
//        try {
//            /**
//             * IOC
//             */
//            return this.injectExtension(this.getAdaptiveExtensionClass().newInstance());
//        } catch (Exception var2) {
//            throw new IllegalStateException("Can not create adaptive extenstion " + this.type + ", cause: " + var2.getMessage(), var2);
//        }
//    }
//
//    private Class<?> getAdaptiveExtensionClass() {
//        this.getExtensionClasses();
//        return this.cachedAdaptiveClass != null ? this.cachedAdaptiveClass : (this.cachedAdaptiveClass = this.createAdaptiveExtensionClass());
//    }
//
//    private Class<?> createAdaptiveExtensionClass() {
//        String code = this.createAdaptiveExtensionClassCode();
//        ClassLoader classLoader = findClassLoader();
//        Compiler compiler = (Compiler)getExtensionLoader(Compiler.class).getAdaptiveExtension();
//        return compiler.compile(code, classLoader);
//    }
//
//    private Map<String, Class<?>> getExtensionClasses() {
//        Map<String, Class<?>> classes = (Map)this.cachedClasses.get();
//        if (classes == null) {
//            synchronized(this.cachedClasses) {
//                classes = (Map)this.cachedClasses.get();
//                if (classes == null) {
//                    classes = this.loadExtensionClasses();
//                    this.cachedClasses.set(classes);
//                }
//            }
//        }
//
//        return classes;
//    }


    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        /**
         * 可行性校验
         */
        if (type == null){
            throw new IllegalArgumentException("Extension type == null");
        }else if (!type.isInterface()){
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        }else if (!withExtensionAnnotation(type)) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not extension, because WITHOUT @" + LXYSPI.class.getSimpleName() + " Annotation!");
        }else{
            ExtensionLoader<T> loader = (ExtensionLoader) EXTENSION_LOADERS.get(type);
            /**
             * 如果没有缓存，初始化并缓存
             */
            if (loader == null) {
                EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader(type));
                loader = (ExtensionLoader)EXTENSION_LOADERS.get(type);
            }
            return loader;
        }
    }
    public static <T> boolean withExtensionAnnotation(Class<T> type){
        return type.isAnnotationPresent(LXYSPI.class);
    }
}
