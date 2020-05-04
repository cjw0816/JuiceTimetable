package com.juice.timetable.data.parse;

import com.juice.timetable.data.bean.ClassNoSignrdItem;
import com.juice.timetable.utils.ReadFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *     author : soreak
 *     e-mail : sorea1k@163.com
 *     time   : 2020/04/29
 *     desc   : nothing
 *     item   : juice
 *     version: 1.0
 * </pre>
 */
public class ParseCheckIn {
    //确认签到
    private static boolean isCheckIn;
    //签到时间
    private static Date checkTime;

    private static List<ClassNoSignrdItem> noSignedList = new ArrayList<>();

    /**
     * 获取自己签到信息
     */
    public static void getMySigned() throws ParseException {
        //定义时间类
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //抓取
        String s = ReadFile.readToString("C:\\Users\\14989\\Desktop\\网页内容\\自己签到记录.html");
        //解析文档
        Document doc = Jsoup.parse(s);
        //提取表格
        Elements elements = doc.getElementsByTag("tbody");
        //System.out.println(elements.html());
        for (Element element : elements) {
            //计算tr标签的数量
            Integer len_Tr = element.getElementsByTag("tr").size();

            Elements ele = element.getElementsByTag("tr").eq(0);
            for (Element el : ele) {
                //tr标签中td的数量
                Integer len_Td = el.getElementsByTag("td").size();

                checkTime = sdf.parse(el.getElementsByTag("td").eq(1).text());
                if ("未签".equals(el.getElementsByTag("td").eq(2).text())) {
                    isCheckIn = false;
                } else {
                    isCheckIn = true;
                }

                System.out.println("签到时间：" + checkTime);
                System.out.println("是否签到：" + isCheckIn);

            }

        }
    }

    /**
     * 获取班级签到信息
     */
    public static void getClassUnSigned() {
        //抓取
        String s = ReadFile.readToString("C:\\Users\\14989\\Desktop\\网页内容\\未签列表.html");
        //解析文档
        Document doc = Jsoup.parse(s);
        //提取表格
        Elements elements = doc.getElementsByTag("tbody");
        //System.out.println(elements.html());
        System.out.println("未签名单");
        for (Element element : elements) {
            //计算tr标签的数量
            Integer len_Tr = element.getElementsByTag("tr").size();
            //循环
            for (int a = 0; a < len_Tr; a++) {
                ClassNoSignrdItem cnsi = new ClassNoSignrdItem();
                Elements ele = element.getElementsByTag("tr").eq(a);
                for (Element el : ele) {
                    //tr标签中td的数量
                    String sno = el.getElementsByTag("td").eq(2).text();
                    cnsi.setSno(sno);
                    String sname = el.getElementsByTag("td").eq(3).text();
                    cnsi.setSname(sname);
                    noSignedList.add(cnsi);
                }
            }
        }
        for (ClassNoSignrdItem item : noSignedList) {
            System.out.println(item);
        }

    }


}
