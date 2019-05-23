//package com.bofa.client.service;
//
//import com.ai.nbs.common.util.io.FileUtil;
//import com.bofa.client.config.NettyHandlerCmpt;
//import com.bofa.entity.BaseSerializableEntity;
//import com.bofa.exception.ChatException;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.function.Function;
//
///**
// * @author Bofa
// * @version 1.0
// * @description com.bofa.client.service
// * @date 2019/4/30
// */
//public abstract class BaseSv<E extends BaseSerializableEntity> {
//
//    protected E entity;
//    protected static Path basePath;
//    protected static String cacheDirectory;
//    private static final String DEFAULT_USER_HOME_IDENTIFIER = "~";
//    static final Logger logger = LoggerFactory.getLogger(BaseSv.class);
//    protected static final Integer DEFAULT_MAX_DEPTH = 3;
//
//    static {
//        cacheDirectory = NettyHandlerCmpt.getProperty("user.cache.directory");
//        if (StringUtils.isEmpty(cacheDirectory)) {
//            ChatException.throwChatException("application.properties中的user.cache.directory不可不为空");
//        }
//        if (cacheDirectory.startsWith(DEFAULT_USER_HOME_IDENTIFIER)) {
//            cacheDirectory = cacheDirectory.replace(DEFAULT_USER_HOME_IDENTIFIER,
//                    System.getProperty("user.home"));
//        }
//
//        Path path = Paths.get(cacheDirectory);
//        if (!Files.exists(path)) {
//            try {
//                Files.createDirectories(path);
//            } catch (IOException e) {
//                logger.error("create directories failed", e);
//            }
//        }
//    }
//
//    protected void createFile(Path filePath) {
//        try {
//            Files.deleteIfExists(filePath);
//            Files.createFile(filePath);
//        } catch (IOException e) {
//            logger.error("create [" + filePath + "] create failed", e);
//        }
//    }
//
//    protected void createDirectory() {
//        /**
//         * simple:
//         * basePath ~/.chat/[userName]/[localDateTime.now1()]
//         */
//        basePath = getPath(BaseSerializableEntity::generateFilePath);
//        if (!Files.exists(basePath)) {
//            try {
//                Files.createDirectories(basePath);
//                System.out.println(basePath + " create successful");
//            } catch (IOException e) {
//                logger.error("create directory failed", e);
//            }
//        }
//    }
//
//    protected void save(Path path) {
//        createFile(path);
//        saveObject(path);
//    }
//
//    protected E get(Path path) {
//        return getEntity(path);
//    }
//
//    protected E getEntity(Path path) {
//        ObjectInputStream ois = null;
//        try {
//            ois = new ObjectInputStream(FileUtil.asInputStream(path));
//            return (E) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (ois != null) {
//                try {
//                    ois.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }
//
//    protected Path getPath(Function<E, String> func) {
//        return Paths.get(cacheDirectory + "/" + func.apply(entity));
//    }
//
//    protected void saveObject(Path path) {
//        ObjectOutputStream oos = null;
//        try {
//            oos = new ObjectOutputStream(FileUtil.asOututStream(path));
//            oos.writeObject(entity);
//        } catch (IOException e) {
//            logger.error("oos writeObject failed", e);
//        } finally {
//            if (oos != null) {
//                try {
//                    oos.close();
//                } catch (IOException e) {
//                    logger.error("oos close failed", e);
//                }
//            }
//        }
//    }
//
//    public E getEntity() {
//        return entity;
//    }
//
//    public void setEntity(E entity) {
//        this.entity = entity;
//    }
//}
