package net.dsdstudio.umk;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
     * 유틸성 함수들 모음
     */
    static class Util {
        public static void log(String $s) {
            System.out.println($s);
        }
    }

    /**
     * BoardData Model
     */
    static class BoardData {
        public BoardDataFactory.BoardType dataType;
        public Integer id;
        public String subject;
        public String detailUrl;
        public String ownerName;
        public String date;

        @Override
        public String toString() {
            return "BoardData{" +
                    "dataType=" + dataType +
                    ", id=" + id +
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
                o.dataType = BoardType.SLR;
                return o;
            } else if ($boardType == BoardType.CLIEN) {
                BoardData o = new BoardData();
                o.id = Integer.valueOf($tds.get(0).html(), 10);
                o.detailUrl = $tds.select(".post_subject").select("a").attr("href");
                o.subject = $tds.select(".post_subject").text();
                o.ownerName = $tds.select(".post_name").select("a").attr("title");
                o.date = $tds.get(4).select("span").attr("title");
                o.dataType = BoardType.CLIEN;
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
                HttpResponse response = Request.Post(loginUrl)
                        .bodyForm(
                                new BasicNameValuePair(paramId, id),
                                new BasicNameValuePair(paramPwd, pwd)
                        )
                        .userAgent(UserAgentStr)
                        .execute().returnResponse();
                Util.log(response.getStatusLine() + " slrclub login succeed.");
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
                String response = new String(Request.Get(bbsUrl)
                        .userAgent(UserAgentStr)
                        .execute().returnContent().asBytes(), "UTF-8");
                return Jsoup.parse(response)
                        .select("#bbs_list tbody tr").stream()
                        .map(tr -> tr.select("td"))
                        .filter(td -> td.select(".list_notice").isEmpty() && Optional.of(td.select(".list_num").html()).isPresent())
                        .map(tds -> BoardDataFactory.getInstance(BoardDataFactory.BoardType.SLR, tds));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
                HttpResponse response = Request.Post(loginUrl)
                        .bodyForm(
                                new BasicNameValuePair(paramId, id),
                                new BasicNameValuePair(paramPwd, pwd)
                        )
                        .userAgent(UserAgentStr)
                        .execute().returnResponse();

                Util.log(response.getStatusLine() + " clien login succeed.");
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

    static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    static final int periodSeconds = 10;

    public static void main(String[] args) {
        SlrBoardDataGrabber slrGrabber = new SlrBoardDataGrabber("your_id", "your_pwd");
        ClienBoardDataGrabber clienGrabber = new ClienBoardDataGrabber("your_id", "your_pwd");
        clienGrabber.login();
        slrGrabber.login();

        service.scheduleAtFixedRate(() -> {
            List<BoardData> clienList = clienGrabber.boardData().collect(Collectors.toList());
            List<BoardData> slrList = slrGrabber.boardData().collect(Collectors.toList());

            OptionalInt slrMaxBoardId = slrList.stream().mapToInt(o -> o.id).reduce(Integer::max);
            OptionalInt clienMaxBoardId = clienList.stream().mapToInt(o -> o.id).reduce(Integer::max);
            Util.log("clien max boardId : " + clienMaxBoardId.getAsInt());
            Util.log("slrclub max boardId : " + slrMaxBoardId.getAsInt());
            Util.log(clienList + " " + slrList);
            Util.log(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS")) + " executed");
        }, 0, periodSeconds, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Util.log("Shutdown Hook started..");
                service.shutdownNow();
                try {
                    service.awaitTermination(0, TimeUnit.MILLISECONDS);
                    Util.log("Shutdown completed..");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
