package net.dsdstudio.umk;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * usedmarket-collector
 * net.dsdstudio.umk
 *
 * @author : bhkim
 * @since : 2015. 1. 9..
 */
public class Main {
    public static void slr(String id, String pwd) {
        try {
            Response r = Request.Post("https://www.slrclub.com/login/process.php")
                    .bodyForm(
                            new BasicNameValuePair("user_id", id),
                            new BasicNameValuePair("password", pwd)
                    )
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
                    .execute();
            // TODO 성공여부 처리
            byte[] b = Request.Get("http://www.slrclub.com/bbs/zboard.php?id=used_market&category=1")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
                    .execute().returnContent().asBytes();
            List<SlrBoardData> list = Jsoup.parse(new String(b)).select("#bbs_list tbody tr").stream()
                    .map(tr -> tr.select("td"))
                    .filter(td -> td.select(".list_notice").isEmpty() && Optional.of(td.select(".list_num").html()).isPresent())
                    .map(SlrBoardData::fromElements)
                    .collect(Collectors.toList());

            Integer maxBbsId = list.stream().map(data -> data.id)
                    .max(Integer::max)
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class SlrBoardData {
        public Integer id;
        public String subject;
        public String detailUrl;
        public String ownerName;
        public String date;

        public static SlrBoardData fromElements(Elements $tds) {
            SlrBoardData o = new SlrBoardData();
            o.id = Integer.valueOf($tds.select(".list_num").html(), 10);
            o.detailUrl = $tds.select(".sbj").select("a").attr("href");
            o.subject = $tds.select(".sbj").select("a").text();
            o.ownerName = $tds.select(".list_name").select("span").text();
            o.date = $tds.select(".list_date").text();
            return o;
        }

        @Override
        public String toString() {
            return "SlrBoardData{" +
                    "id=" + id +
                    ", subject='" + subject + '\'' +
                    ", detailUrl='" + detailUrl + '\'' +
                    ", ownerName='" + ownerName + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

    static class ClienBoardData {
        public Integer id;
        public String subject;
        public String detailUrl;
        public String ownerName;
        public String date;

        public static ClienBoardData fromElements(Elements $tds) {
            ClienBoardData o = new ClienBoardData();
            o.id = Integer.valueOf($tds.get(0).html(), 10);
            o.detailUrl = $tds.select(".post_subject").select("a").attr("href");
            o.subject = $tds.select(".post_subject").text();
            o.ownerName = $tds.select(".post_name").select("a").attr("title");
            o.date = $tds.get(4).select("span").attr("title");
            return o;
        }

        @Override
        public String toString() {
            return "ClienBoardData{" +
                    "id=" + id +
                    ", subject='" + subject + '\'' +
                    ", detailUrl='" + detailUrl + '\'' +
                    ", ownerName='" + ownerName + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

    public static void clien(String $id, String $pwd) {
        try {
            Request.Post("https://www.clien.net/cs2/bbs/login_check.php")
                    .bodyForm(
                            new BasicNameValuePair("mb_id", $id),
                            new BasicNameValuePair("mb_password", $pwd)
                    )
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
                    .execute();
            byte[] b = Request.Get("http://www.clien.net/cs2/bbs/board.php?bo_table=sold&sca=[%ED%8C%90%EB%A7%A4]")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
                    .execute().returnContent().asBytes();
            List<ClienBoardData> list = Jsoup.parse(new String(b)).select(".board_main tr.mytr").stream()
                    .map(e -> e.select("td"))
                    .map(ClienBoardData::fromElements)
                    .collect(Collectors.toList());

            Integer maxBbsId = list.stream().map(o -> o.id)
                    .max(Integer::max)
                    .get();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    
    }
}
