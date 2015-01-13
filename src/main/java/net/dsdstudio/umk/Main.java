package net.dsdstudio.umk;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * usedmarket-collector
 * net.dsdstudio.umk
 *
 * @author : bhkim
 * @since : 2015. 1. 9..
 */
public class Main {
    public static final String UserAgentStr = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";

    /**
     * BoardData Model
     */
    static class BoardData {
        public Integer id;
        public String subject;
        public String detailUrl;
        public String ownerName;
        public String date;

        @Override
        public String toString() {
            return "BoardData{" +
                    "id=" + id +
                    ", subject='" + subject + '\'' +
                    ", detailUrl='" + detailUrl + '\'' +
                    ", ownerName='" + ownerName + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

    /**
     * BoardData 생성 역할을 담당하는 Factory Class
     */
    static class BoardDataFactory {
        enum BoardType {
            SLR, CLIEN
        }

        static BoardData getInstance(BoardType $boardType, Elements $tds) {
            if ($boardType == BoardType.SLR) {
                BoardData o = new BoardData();
                o.id = Integer.valueOf($tds.select(".list_num").html(), 10);
                o.detailUrl = $tds.select(".sbj").select("a").attr("href");
                o.subject = $tds.select(".sbj").select("a").text();
                o.ownerName = $tds.select(".list_name").select("span").text();
                o.date = $tds.select(".list_date").text();
                return o;
            } else if ($boardType == BoardType.CLIEN) {
                BoardData o = new BoardData();
                o.id = Integer.valueOf($tds.get(0).html(), 10);
                o.detailUrl = $tds.select(".post_subject").select("a").attr("href");
                o.subject = $tds.select(".post_subject").text();
                o.ownerName = $tds.select(".post_name").select("a").attr("title");
                o.date = $tds.get(4).select("span").attr("title");
                return o;
            } else {
                throw new IllegalArgumentException("잘못된 인자입니다. ");
            }

        }
    }

    /**
     * Board데이터 수집기 인터페이스
     */
    interface Grabber {
        void login();

        Boolean isLogined();

        Stream<BoardData> boardData();
    }

    /**
     * Slrclub boarddata 수집용 구현체
     */
    static class SlrBoardDataGrabber implements Grabber {
        private final String id;
        private final String pwd;

        private final String loginUrl = "https://www.slrclub.com/login/process.php";
        private final String bbsUrl = "http://www.slrclub.com/bbs/zboard.php?id=used_market&category=1";
        private final String paramId = "user_id";
        private final String paramPwd = "password";

        private Boolean isLogin = false;

        public SlrBoardDataGrabber(String $id, String $pwd) {
            this.id = $id;
            this.pwd = $pwd;
        }

        @Override
        public void login() {
            try {
                Request.Post(loginUrl)
                        .bodyForm(
                                new BasicNameValuePair(paramId, id),
                                new BasicNameValuePair(paramPwd, pwd)
                        )
                        .userAgent(UserAgentStr)
                        .execute();
                this.isLogin = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Boolean isLogined() {
            return this.isLogin;
        }

        @Override
        public Stream<BoardData> boardData() {
            String response = null;
            try {
                response = new String(Request.Get(bbsUrl)
                        .userAgent(UserAgentStr)
                        .execute().returnContent().asBytes(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Jsoup.parse(response)
                    .select("#bbs_list tbody tr").stream()
                    .map(tr -> tr.select("td"))
                    .filter(td -> td.select(".list_notice").isEmpty() && Optional.of(td.select(".list_num").html()).isPresent())
                    .map(tds -> BoardDataFactory.getInstance(BoardDataFactory.BoardType.SLR, tds));
        }
    }


    static class ClienBoardDataGrabber implements Grabber {
        private final String id;
        private final String pwd;

        private final String loginUrl = "https://www.clien.net/cs2/bbs/login_check.php";
        private final String bbsUrl = "http://www.clien.net/cs2/bbs/board.php?bo_table=sold&sca=[%ED%8C%90%EB%A7%A4]";
        private final String paramId = "mb_id";
        private final String paramPwd = "mb_password";
        private Boolean isLogin = false;

        public ClienBoardDataGrabber(String $id, String $pwd) {
            this.id = $id;
            this.pwd = $pwd;
        }

        @Override
        public void login() {
            try {
                Request.Post(loginUrl)
                        .bodyForm(
                                new BasicNameValuePair(paramId, id),
                                new BasicNameValuePair(paramPwd, pwd)
                        )
                        .userAgent(UserAgentStr)
                        .execute();
                this.isLogin = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Boolean isLogined() {
            return this.isLogin;
        }

        @Override
        public Stream<BoardData> boardData() {

            try {
                String response = new String(Request.Get(this.bbsUrl)
                        .userAgent(UserAgentStr)
                        .execute().returnContent().asBytes(), "UTF-8");

                return Jsoup.parse(response).select(".board_main tr.mytr").stream()
                        .map(tr -> tr.select("td"))
                        .map(tds -> BoardDataFactory.getInstance(BoardDataFactory.BoardType.CLIEN, tds));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void slr(String $id, String $pwd) {
        SlrBoardDataGrabber grabber = new SlrBoardDataGrabber($id, $pwd);

        grabber.login();
        grabber.boardData().forEach(System.out::println);

    }

    public static void clien(String $id, String $pwd) {
        ClienBoardDataGrabber grabber = new ClienBoardDataGrabber($id, $pwd);

        grabber.login();
        grabber.boardData().forEach(System.out::println);
    }

    public static void main(String[] args) {
    }
}
