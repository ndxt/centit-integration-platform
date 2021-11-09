package com.centit.framework.users.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 使用磁盘文件模拟accessToken和jsTicket的数据持久化
 * 正式项目请是写入到Mysql等持久化存储
 *
 * @author openapi@dingtalk
 * @date 2020/2/4
 */
public class FileUtil {

    private static final String FILEPATH = "Permanent_Data";

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 将json写入文件
     *
     * @param json     需要写入的json对象
     * @param fileName 文件名称
     */
    public synchronized static void write2File(Object json, String fileName) {
        File filePath = new File(FILEPATH);
        JSONObject eJson = null;

        if (!filePath.exists() && !filePath.isDirectory()) {
            filePath.mkdirs();
        }

        File file = new File(FILEPATH + File.separator + fileName + ".xml");
        logger.info("path：{}, abs path {}", file.getPath(), file.getAbsolutePath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                logger.error("createNewFile异常", e);
            }
        } else {
            eJson = read2JSON(fileName);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            if (eJson == null) {
                writer.write(json.toString());
            } else {
                Object[] array = ((JSONObject)json).keySet().toArray();
                for (Object o : array) {
                    eJson.put(o.toString(), ((JSONObject)json).get(o.toString()));
                }

                writer.write(eJson.toString());
            }

        } catch (IOException e) {
            logger.error("write2File异常", e);
        }
    }

    /**
     * 读文件到json
     *
     * @param fileName 文件名称
     * @return 文件内容Json对象
     */
    public static JSONObject read2JSON(String fileName) {
        File file = new File(FILEPATH + File.separator + fileName + ".xml");
        if (!file.exists()) {
            return null;
        }

        StringBuilder lastStr = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                lastStr.append(tempString);
            }
        } catch (IOException e) {
            logger.error("read2JSON异常", e);
        }

        return (JSONObject) JSON.parse(lastStr.toString());
    }

    /**
     * 通过key值获取文件中的value
     *
     * @param fileName 文件名称
     * @param key      key值
     * @return key对应的value
     */
    public static Object getValue(String fileName, String key) {
        JSONObject eJSON = null;
        eJSON = read2JSON(fileName);
        if (null != eJSON && eJSON.containsKey(key)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> values = JSON.parseObject(eJSON.toString(), Map.class);
            return values.get(key);
        } else {
            return null;
        }
    }

    public static Map<Long, Long> toHashMap(JSONObject js) {
        if (js == null) {
            return null;
        }
        HashMap<Long, Long> data = new HashMap<>();
        // 将json字符串转换成jsonObject
        Set<String> set = js.keySet();
        // 遍历jsonObject数据，添加到Map对象
        for (String s : set) {
            String key = String.valueOf(s);
            Long keyLong = Long.valueOf(key);

            String value = js.getString(key);
            Long valueLong;
            if (TextUtils.isEmpty(value)) {
                valueLong = js.getLong(key);
            } else {
                valueLong = Long.valueOf(value);
            }
            data.put(keyLong, valueLong);
        }
        return data;
    }
}
