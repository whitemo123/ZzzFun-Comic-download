import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws Exception {
        System.out.println("请输入你这个憨批想看的剧名");
        Scanner in = new Scanner(System.in);
        String str = search(in.next());
        System.out.println("请输入你这个憨批想看第几集");
        Scanner s = new Scanner(System.in);
        System.out.println(getUrl(str, s.next()));
    }
    
    /*
    *搜索动漫
    *str 动漫名字
    */
    private static String search(String str) throws Exception {
        return doPost("http://service-agbhuggw-1259251677.gz.apigw.tencentcs.com:80/android/search", 
            "userid=347779&key=" + str, 
        1);
    }
    
    /*
     *得到视频地址
     *id 视频id
     *ji 视频集数
     */
    private static String getUrl(String id, String ji) throws Exception {
        return doPost("http://service-agbhuggw-1259251677.gz.apigw.tencentcs.com:80/android/video/newplay", 
            "playid="+id+"-"+ ji + "-a&sing=76b40730a205e7d098542d42e8be6978&userid=347779&apptoken=7858cdd24271830a407af60915960054&map=1586424379205", 
        2);
    }
    
    private static String doPost(String url, String params, int type) throws Exception {
        String videoUrl = "";
        URL urls = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.connect();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
        writer.write(params);
        writer.close();

        InputStream is =  conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String inputLine;
        StringBuilder result = new StringBuilder("");
        if((inputLine = br.readLine()) != null){
            result.append(inputLine);
        }
        conn.disconnect();
        if(type == 1) {
            JSONObject json = new JSONObject(result.toString());
            JSONArray data = json.getJSONArray("data");
            if(data.length() > 1) {
                for(int i = 0; i < data.length(); i++) {
                    JSONObject js = (JSONObject) data.get(i);
                    String name = js.getString("videoName");
                    String videoId = js.getString("videoId");
                    String jis = js.getString("videoremarks");
                    System.out.println(name + "    " + videoId + "    " + jis);
                }
                System.out.println("请输入选择好的id");
                Scanner in = new Scanner(System.in);
                return in.next();
            } else if(data.length() == 1){
                JSONObject js = (JSONObject) data.get(0);
                videoUrl = js.getString("videoId");
                System.out.println(js.getString("videoremarks"));
            }
        } else if(type == 2) {
            JSONObject json = new JSONObject(result.toString());
            String data = json.getString("data");
            JSONObject js = new JSONObject(data);
            videoUrl = js.getString("videoplayurl");
        }
        return videoUrl;
    }
}
