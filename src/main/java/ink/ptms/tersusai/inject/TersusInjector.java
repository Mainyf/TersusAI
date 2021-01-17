package ink.ptms.tersusai.inject;

import ink.ptms.tersusai.TersusAI;
import io.izzel.taboolib.Version;
import io.izzel.taboolib.module.inject.TFunction;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.lite.SimpleVersionControl;
import io.izzel.taboolib.module.locale.logger.TLogger;
import org.bukkit.entity.LivingEntity;

import java.io.IOException;
import java.util.Optional;

/**
 * @Author sky
 * @Since 2019-08-23 11:00
 */
public abstract class TersusInjector {

    @TInject
    static TLogger logger;
    @TInject(asm = "ink.ptms.tersusai.inject.TersusInjectorImpl")
    static TersusInjector injector;
    static TersusSelector generator;

    static byte[][] source = new byte[9][];

    @TFunction.Load
    static void init() throws IllegalAccessException, InstantiationException, IOException {
        switch (Version.getCurrentVersion()) {
            case v1_8:
                generator = (TersusSelector) SimpleVersionControl.createNMS("ink.ptms.tersusai.inject.impl.Injector10800").translate(TersusAI.INSTANCE.getPlugin()).newInstance();
                break;
            case v1_9:
            case v1_10:
            case v1_11:
            case v1_12:
                generator = (TersusSelector) SimpleVersionControl.createNMS("ink.ptms.tersusai.inject.impl.Injector10900").translate(TersusAI.INSTANCE.getPlugin()).newInstance();
                break;
            case v1_13:
                generator = (TersusSelector) SimpleVersionControl.createNMS("ink.ptms.tersusai.inject.impl.Injector11300").translate(TersusAI.INSTANCE.getPlugin()).newInstance();
                break;
            case v1_14:
            case v1_15:
                generator = (TersusSelector) SimpleVersionControl.createNMS("ink.ptms.tersusai.inject.impl.Injector11400").translate(TersusAI.INSTANCE.getPlugin()).newInstance();
                break;
            case v1_16:
                generator = (TersusSelector) SimpleVersionControl.createNMS("ink.ptms.tersusai.inject.impl.Injector11600").translate(TersusAI.INSTANCE.getPlugin()).newInstance();
                break;
            default:
                logger.error("TersusAI doesn't support the current version \"" + Version.getCurrentVersion() + "\n.");
                break;
        }
    }

    public static void level(LivingEntity entity, int level) {
        Optional.ofNullable(injector.getGoal0(entity)).ifPresent(i -> i.setLevel(level));
        Optional.ofNullable(injector.getTarget0(entity)).ifPresent(i -> i.setLevel(level));
    }

    public static void inject(LivingEntity entity) {
        injectGoal(entity);
        injectTarget(entity);
    }

    public static void injectGoal(LivingEntity entity) {
        try {
            injector.setGoal0(entity, generator.generateGoal(entity));
        } catch (NullPointerException ignored) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void injectTarget(LivingEntity entity) {
        try {
            injector.setTarget0(entity, generator.generateTarget(entity));
        } catch (NullPointerException ignored) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static TersusSelector getGoal(LivingEntity entity) {
        return injector.getGoal0(entity);
    }

    public static TersusSelector getTarget(LivingEntity entity) {
        return injector.getTarget0(entity);
    }

    public static boolean isInsentient(LivingEntity entity) {
        return injector.isInsentient0(entity);
    }

    public static boolean isInjected(LivingEntity entity) {
        return injector.getGoal0(entity) != null && injector.getTarget0(entity) != null;
    }

    abstract public TersusSelector getGoal0(LivingEntity entity);

    abstract public TersusSelector getTarget0(LivingEntity entity);

    abstract public void setGoal0(LivingEntity entity, TersusSelector selector);

    abstract public void setTarget0(LivingEntity entity, TersusSelector selector);

    abstract public boolean isInsentient0(LivingEntity entity);

//    static class Loader extends ClassLoader {
//
//        private Loader() {
//            super(Loader.class.getClassLoader());
//        }
//
//        public static Class<?> of(String name, byte[] arr) throws IOException {
//            SimpleVersionControl control = SimpleVersionControl.create().useNMS().plugin(TersusAI.getPlugin());
//            ClassReader classReader = new ClassReader(new ByteArrayInputStream(arr));
//            ClassWriter classWriter = new ClassWriter(0);
//            ClassVisitor classVisitor = new SimpleClassVisitor(control, classWriter);
//            classReader.accept(classVisitor, 8);
//            classWriter.visitEnd();
//            classVisitor.visitEnd();
//            arr = classWriter.toByteArray();
//            return new Loader().defineClass(name, arr, 0, arr.length, Loader.class.getProtectionDomain());
//        }
//
//        public static byte[] fromList(List<Byte> list) {
//            byte[] arr = new byte[list.size()];
//            for (int i = 0; i < list.size(); i++) {
//                arr[i] = list.get(i);
//            }
//            list.clear();
//            return arr;
//        }
//    }
}
