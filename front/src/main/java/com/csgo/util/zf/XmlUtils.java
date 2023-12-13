package com.csgo.util.zf;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.util.*;

/**
 * Created by Arya on 2017/11/3.
 */
public class XmlUtils {
    public static Map<String, Object> Dom2Map(Document doc) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
            Element e = (Element) iterator.next();
            List list = e.elements();
            if (list.size() > 0) {
                map.put(e.getName(), Dom2Map(e));
            } else
                map.put(e.getName(), e.getText());
        }
        return map;
    }

    /**
     * XML字符串转换JSON对象
     *
     * @param xmlStr XML字符串
     * @return JSON对象
     */
    public static JSONObject xmlToJson(String xmlStr) {
        JSONObject result = new JSONObject(true);
        SAXReader xmlReader = new SAXReader();
        try {
            Document document = xmlReader.read(new StringReader(xmlStr));
            Element element = document.getRootElement();
            return xmlToJson(element, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * XML节点转换JSON对象
     *
     * @param element 节点
     * @param object  新的JSON存储
     * @return JSON对象
     */
    private static JSONObject xmlToJson(Element element, JSONObject object) {
        List<Element> elements = element.elements();
        for (Element child : elements) {
            Object value = object.get(child.getName());
            Object newValue;

            if (child.elements().size() > 0) {
                JSONObject jsonObject = xmlToJson(child, new JSONObject(true));
                if (!jsonObject.isEmpty()) {
                    newValue = jsonObject;
                } else {
                    newValue = child.getText();
                }
            } else {
                newValue = child.getText();
            }

            List<Attribute> attributes = child.attributes();
            if (!attributes.isEmpty()) {
                JSONObject attrJsonObject = new JSONObject();
                for (Attribute attribute : attributes) {
                    attrJsonObject.put(attribute.getName(), attribute.getText());
                    attrJsonObject.put("content", newValue);
                }
                newValue = attrJsonObject;
            }

            if (newValue != null) {
                if (value != null) {
                    if (value instanceof JSONArray) {
                        ((JSONArray) value).add(newValue);
                    } else {
                        JSONArray array = new JSONArray();
                        array.add(value);
                        array.add(newValue);
                        object.put(child.getName(), array);
                    }
                } else {
                    object.put(child.getName(), newValue);
                }
            }
        }
        return object;
    }

    public static Map Dom2Map(Element e) {
        Map map = new HashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = Dom2Map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }

    /*public static void main(String[] args) {
        String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyB8jDkhaiBI6pa9IT5DIHz+x7nogyUm1WjjyU4A2w2IwEzXvr3DrYluXLq4VZ0mbEfgsrI/vnA5eC6zF8idwiMbdWkCFUx7B90zM9D84QB7HSnmmCq4DyON21TUQhOk6VN9X5Rd6Xs2bZedm27bmdTVKX7cigKT+C3hGJ8ifVCqaT+bsNPlklgIyRmkrLX0Ycz5uM7mjNS2SgBfbj5hrbW/5FA1Lk1qxg2r7/SoC8p7ObzELSoOGfZE3IaXDh9t5VcnPE5x6SiyWWspLw1ZhLnMVCl487GccBpg6As+q+KDlkxsFabPbCuV3uG2X/rZc4kg0PR+6+P1V4q9DfRG+ewIDAQAB";
        String clientPrivateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDS+DoiJKe1wt59PikjGndp52utV7uH1ll0dAuvFdffE2jVT66cKQXwxdvMcKn+6flgAGv+sekdbb8mWFvkD22H8OBRzZQarextfdPDfmGqCIp2dLXbIUKlGGGm71heQ4+qHhBPlfuXSd+ILgcg/bg6MweZ19tsX7sdZq+xV+XwQOVB6yHMJ39X1OTTlhbztwun+UlgbycAN1iL5zX0lrZ12KKL5i/p7B/4qVBo1D9spyrLzKg9eppqJnCQD6SXH+j4YDqXlZbRNrFb0Cjh51HqsDVW/aW0/druSI48zhYyvh5tRd+EwVsSgMQ0CYXNwY3Xze/hMsyTXvDPasu/gWn/AgMBAAECggEAH3ixL2O5cwdpalSKDXcGxlEkAn1ad9aVjKY9EfbXt6rtEhkrapdCEBtsARDzgxgDP/uAIMQIiv/SqpU2zAwzIjrgM8PPqrU0VlcVbo3dYuCYSzh44oIRL5C2DJGa5KD+fvmyJDUzDWwTQPzgyl6fC2uOYDhPvth/ENE/N1zt68t03ops2wQk4i4ajkIturhw6rdCtzVoQPlputWy2nZ8x2l3A44K2qF3jcnCHMtYkGvJ5GiR9EFUVeoFFW7BLo+kG6IKu3nc9tem3TSF/TqnHdTco+4YozGcgw4BcBxag4riIgl5N2f6QbQm9skD9rltwDdBFGF4iCM9WLSV/0JbhQKBgQDS+y4AJY+RJTrxkzc4M2jusxJ3XLpDvdjUctvanWL2+1UsJmg+8uwGCQZGvsvuCoDCaKLaObZ7Ws8p6ohnPhpIgj+8q0EKJLY647+aGnBNvyNbqO87yrFPLZPWpflCVDam1NXGgfdY5sIF4ZiGUE8fj3FWKY37Z8fV0PE6F4NdEwKBgQD//GrY2B90bYS75kLTd86MUYBH4ANM6B4OQfyiwbOhXkDWCZtCDlYMrf7R0qJAZTD90kSspW0lCXCfkqqCFr0aM9kgpp9mzfoCXg12eP9DG1Q5uD/Pd5MwNG5e5ZAP2WmjON8/Vg2WnDnmvZeJQeQ4KsWzRUdJIpapJF3fbjE45QKBgCin+IxUsJ7CsuIcgVaWKZFd9qADwW0OZ3tBCvbMv2Zitl4EpLAEtdDP6mK1n6ymX3uXGFK/LlgE/sjQBisG/5+bYdbklWrz+h3NB34VvDBvNcwS+M7n11Uvrj59y1Op0MJDX1KaduStyxPBDlhLn4Owa6Gz7kVVEWA1nFaac1j7AoGACTrVs2nfgHgucAP2Zt+L//00TeMi21QHOWZhsJChZHEcj1sbUP9pVN21MWWR2haL9KrvNPZmAA7eE7LbGQOu39H8wzOezbshh5JSk/H66jWDDO26x0U9dmB9lkhasGebanozenok1UhewhYV76oDi47nP/aAgn+PxRt9kv/LII0CgYEAupP8++vKlCysbI64icQtrTCCmmrHY7swt9Zaui6gRHPRaN7hBRrvFrRrFDvy8NdPd3w9roKrgVcIGLWfBTJ+5FmQoMuQCrwbRHsstNnMZPz8TOspmQL2LN7+/aQNqgC0BHepS9AKpFXsCA2Yf7vQx+0DfS1cYzvEEYpU4GJnr1E=";
        String result ="<?xml version=\"1.0\" encoding=\"utf-8\"?><root><ret_code>0000</ret_code><ret_msg>已经发送短信验证码</ret_msg><encrypt_data><![CDATA[Rgh5rJ3slb8Jy2XNWn5XnPsr9hUKN/Ryci7PBODo3TSkctnYF9wJ1eTZHy0W0eQfpY6ftHh1MSqmOGmrT/Go6m1Wquls9BBvDtJDSpu0E1zXRzYjXCYx3FmgN1tiR2t1+XxVPKOGFXV6zb5d/ql5E7evAzs1QydOsyuC6ptNFnRrOuuj9OPLxftnSHzYXbSaCL3tUcUmsnPQwL4UOQzHn3XQjzA93D3SNhVfOuKUg2kOPiTfO4B+iAn7y+ymwl+sN76YvX3WsuHubVSzs7gaSwX70tOKHK4FjvK5OfH1jG297fBT2nMs0zVgdjyKXRNUl4x9USHn2e5WembamAXChw==]]></encrypt_data><sign><![CDATA[s34ZvlZBZNqllZYztJMWeRWnGiBSG/jiQiqhHtfC+0BLGScn4/wQ/3qW6FmA7/7C54CdDciAJzPXhgs7dS4OT35GsCt0d0y6ezrSnDgAe4cfD35TuFSgzHnsA3spI+G/Arh4UQqK1NLlLU5wGNrbi0YekxQJM8l20VVHz/CPU4Th4ymuEBEdlqWi1RNlj8cUxs/RCjNi6TXVXoI8lFqOKfw2JLVtvDRQtH617TfhMvvVkaUF37IKepmUN51kq96tMQIiu1tEFhMxMnwhUZBFZwk81J5HI86jwRsyzO+fUBAOoxvQwMFoLgBcqJwM2i90g4eIROJvOWtzT9BX5H+cTg==]]></sign></root>";
        JSONObject jsonObject = XmlUtils.xmlToJson(result);
        PaySmsResponse paySmsResponse = JSON.parseObject(jsonObject.toJSONString(), PaySmsResponse.class);
        String resultEncryptData = paySmsResponse.getEncrypt_data();
        String resultSign = paySmsResponse.getSign();
        SignEncrypt signEncrypt = new SignEncrypt();
        try {
            String resultData = signEncrypt.decrypt(resultEncryptData, signEncrypt.getPrivateKey(clientPrivateKeyStr));
            System.out.println(resultData);
            boolean verify = signEncrypt.verify(resultData, resultSign, signEncrypt.getPublicKey(publicKeyStr));
            System.out.println(verify);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }*/
}
