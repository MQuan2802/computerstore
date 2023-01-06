package controller.zaloorder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.crypto.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import controller.Manageorder;
import controller.jwt.Tokenmanager;
import controller.userloginmanage.MyUserDetail;
import controller.vn.zalopay.crypto.HMACUtil;
import model.order;
import model.orderitem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONObject; // https://mvnrepository.com/artifact/org.json/json
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.logging.Logger;

@Controller
@RequestMapping("/order")
public class createorder {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String key2 = "eG4r0GcoNtRGbO8";
    private Mac HmacSHA256;

    public createorder() throws Exception {
        HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(key2.getBytes(), "HmacSHA256"));
    }

    @Autowired
    Tokenmanager tokenmanager;

    private static Map<String, String> config = new HashMap<String, String>() {
        {
            put("appid", "553");
            put("key1", "9phuAOYhan4urywHTh0ndEXiV3pKHr5Q");
            put("key2", "Iyz2habzyr7AG8SgvoBCbKwKi3UzlLi3");
            put("endpoint", "https://sandbox.zalopay.com.vn/v001/tpe/createorder");
        }
    };

    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    @GetMapping("/zalopayredirect")
    // @ResponseBody
    public ResponseEntity zalopayredirect(@RequestParam Map<String, String> data) {
        String checksumData = data.get("appid") + "|" + data.get("apptransid") + "|" + data.get("pmcid") + "|"
                + data.get("bankcode") + "|" +
                data.get("amount") + "|" + data.get("discountamount") + "|" + data.get("status");
        byte[] checksumBytes = HmacSHA256.doFinal(checksumData.getBytes());
        String checksum = DatatypeConverter.printHexBinary(checksumBytes).toLowerCase();

        JSONObject result = new JSONObject();
        if (!checksum.equals(data.get("checksum"))) {
            return ResponseEntity.badRequest().body("Bad Request");
        } else {
            String transid = (String) data.get("apptransid");
            String orderIDstr = transid.substring(7);
            try {
                if (Manageorder.isPaid(Integer.valueOf(orderIDstr))) {
                    return ResponseEntity.ok("successfully paid");
                } else {
                    PostOrder PostOrder = new PostOrder(Integer.valueOf(orderIDstr), 0, transid);
                    int zaloorderstatus = PostOrder.queryOrderstatus();
                    if (zaloorderstatus == 1)
                        if (Manageorder.UpdateOrderStatus(Integer.valueOf(orderIDstr), 2, 1))
                            return ResponseEntity.ok("successfully paid");
                    return ResponseEntity.badRequest().body("there is an error in the system");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return ResponseEntity.badRequest().body("there is an error in the system");
            }
            // kiểm tra xem đã nhận được callback hay chưa, nếu chưa thì tiến hành gọi API
            // truy vấn trạng thái thanh toán của đơn hàng để lấy kết quả cuối cùng

        }

    }

    @PostMapping("/zalopayorder")
    public String createzaloorder(HttpServletRequest req)
            throws ClientProtocolException, IOException {
        String token = req.getParameter("token");
        int orderID = tokenmanager.getOrderIDFromToken(token);
        String checkout = "";
        if (orderID > 0) {
            Long total = Long.valueOf(0);
            order DBorder = Manageorder.getcart(orderID);
            if (DBorder != null) {
                final Map embeddata = new HashMap() {
                    {
                        put("merchantinfo", "embeddata123");
                    }
                };

                Map[] items = {};
                List<HashMap<String, String>> itemlist = new ArrayList<>();
                for (orderitem item : DBorder.getorderitem()) {
                    HashMap itemmap = new HashMap<>();
                    itemmap.put("prodid", item.getproducts().getprodid());
                    itemmap.put("prodname", item.getproducts().getnameonorder());
                    itemmap.put("itemprice", item.getproducts().getprice());
                    itemmap.put("itemquantity", item.getquantity());
                }
                items = itemlist.toArray(items);
                final Map[] FinalItemMap = items;
                long apptime = System.currentTimeMillis();
                String transid = getCurrentTimeString("yyMMdd") + "_" + DBorder.getorderid();
                Map<String, Object> order = new HashMap<String, Object>() {
                    {
                        put("appid", config.get("appid"));
                        put("apptransid", transid); // mã giao
                        // dich có
                        // định
                        // dạng
                        // yyMMdd_xxxx
                        put("apptime", apptime); // miliseconds
                        put("appuser", "demo");
                        put("amount", 500);
                        put("description", "ZaloPay Intergration Demo");
                        put("bankcode", "zalopayapp");
                        put("item", new JSONObject(FinalItemMap).toString());
                        put("embeddata", new JSONObject(embeddata).toString());
                    }
                };

                // appid +”|”+ apptransid +”|”+ appuser +”|”+ amount +"|" + apptime +”|”+
                // embeddata +"|" +item
                String data = order.get("appid") + "|" + order.get("apptransid") + "|" + order.get("appuser") + "|"
                        + order.get("amount")
                        + "|" + order.get("apptime") + "|" + order.get("embeddata") + "|" + order.get("item");
                order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data));

                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = new HttpPost(config.get("endpoint"));

                List<NameValuePair> params = new ArrayList<>();
                for (Map.Entry<String, Object> e : order.entrySet()) {
                    params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
                }

                // Content-Type: application/x-www-form-urlencoded
                post.setEntity(new UrlEncodedFormEntity(params));

                CloseableHttpResponse res = client.execute(post);
                BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
                StringBuilder resultJsonStr = new StringBuilder();
                String line;

                while ((line = rd.readLine()) != null) {
                    resultJsonStr.append(line);
                }

                JSONObject result = new JSONObject(resultJsonStr.toString());
                int rturncode = (int) result.get("returncode");
                if (1 == rturncode) {
                    PostOrder PostOrder = new PostOrder(DBorder.getorderid(), apptime, transid);
                    PostOrder.start();
                    return "redirect:" + result.get("orderurl");
                }
            }
        }
        return "QRfail";
    }

    @PostMapping("/createDBorder")
    public String createDBorder(HttpServletRequest req) {
        String prodid = req.getParameter("prodid");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MyUserDetail) {
            String customerID = ((MyUserDetail) principal).getid();

            Manageorder.createorder(customerID, prodid);
            // Manageorder.UpdateItem(8, "2060ahihi", 69);
        }

        return "redirect:/cart";
    }

    @PostMapping("/callback")
    public String callback(HttpServlet req, @RequestBody String jsonStr) {
        JSONObject result = new JSONObject();
        System.out.println("ahihi" + jsonStr);
        try {
            JSONObject cbdata = new JSONObject(jsonStr);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            // check if the callback is valid (from ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback is invalid
                result.put("returncode", -1);
                result.put("returnmessage", "mac not equal");
            } else {
                // payment success
                // merchant update status for order's status
                JSONObject data = new JSONObject(dataStr);
                String transid = (String) data.get("apptransid");
                String orderIDstr = transid.substring(7);
                Manageorder.UpdateOrderStatus(Integer.parseInt(orderIDstr), 2, 1);
                logger.info("update order's status = success where apptransid = " + data.getString("apptransid"));

                result.put("returncode", 1);
                result.put("returnmessage", "success");
            }
        } catch (Exception ex) {
            result.put("returncode", 0); // callback again (up to 3 times)
            result.put("returnmessage", ex.getMessage());
        }

        // returns the result for ZaloPay server
        return result.toString();
    }
}

class PostOrder extends Thread {
    private int orderid;
    private long apptime;
    private String transid;

    private static Map<String, String> config = new HashMap<String, String>() {
        {
            put("appid", "553");
            put("key1", "9phuAOYhan4urywHTh0ndEXiV3pKHr5Q");
            put("key2", "Iyz2habzyr7AG8SgvoBCbKwKi3UzlLi3");
            put("endpoint", "https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid");
        }
    };

    public PostOrder(int orderid, long apptime, String transid) {

        this.orderid = orderid;
        this.apptime = apptime;
        this.transid = transid;
    }

    public PostOrder() {
    }

    public void run() {
        PostOrder PostOrder = new PostOrder(this.orderid, this.apptime, this.transid);
        int orderqueryresult;
        try {
            do {
                orderqueryresult = PostOrder.queryOrderstatus();
                Thread.sleep(20000);
            } while ((orderqueryresult == 0 || PostOrder.queryOrderstatus() == -49
                    || PostOrder.queryOrderstatus() == -117)
                    && System.currentTimeMillis() - apptime <= 15 * 1000 * 60);
            if (orderqueryresult == 1 && System.currentTimeMillis() - apptime <= 15 *
                    1000 * 60) {
                if (Manageorder.isPaid(orderid)) {
                    int updatetime = 0;
                    boolean UpdateStatusRes;
                    Manageorder.UpdateOrderStatus(orderid, 2, 1);
                    do {
                        updatetime++;
                        UpdateStatusRes = Manageorder.UpdateOrderStatus(orderid, 2, 1);
                    } while (updatetime < 3 && !UpdateStatusRes);
                    if (!UpdateStatusRes)
                        System.err.println("fail update status orderid " + orderid + "to 1");
                }
            } else {
                System.err.println("fail query status orderid" + orderid + " with time " +
                        System.currentTimeMillis()
                        + " and query result code " + orderqueryresult);
            }

        } catch (ClientProtocolException e) {
            System.err.println("fail query status orderid" + orderid);
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("fail query status orderid" + orderid);
            // TODO Auto-generated catch block
            System.err.println("fail query status orderid" + orderid);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("fail query status orderid" + orderid);
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (

        InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int queryOrderstatus() throws URISyntaxException, ClientProtocolException, IOException {
        String apptransid = "190308_123456";
        String data = config.get("appid") + "|" + apptransid + "|" + config.get("key1"); // appid|apptransid|key1
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", config.get("appid")));
        params.add(new BasicNameValuePair("apptransid", apptransid));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(config.get("endpoint"));
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri.build());

        CloseableHttpResponse res = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        for (String key : result.keySet()) {
            System.out.format("%s = %s\n", key, result.get(key));
        }
        if ((boolean) result.get("isprocessing")) {
            return 0;
        }
        return (int) result.get("returncode");
    }
}
