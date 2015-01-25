package net.dsdstudio.umk.services;

/**
 * usedmarket-collector net.dsdstudio.umk.services
 *
 * @author : bhkim
 * @since : 2015-01-22 오후 10:40
 */

import net.dsdstudio.umk.BoardData;
import net.dsdstudio.umk.utils.Util;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Slrclub boarddata 수집용 구현체
 */
@Service
public class SlrBoardDataGrabService implements GrabService {
    private String id;
    private String pwd;

    private final String loginUrl = "https://www.slrclub.com/login/process.php";
    private final String bbsUrl = "http://www.slrclub.com/bbs/zboard.php?id=used_market&category=1";
    private final String paramId = "user_id";
    private final String paramPwd = "password";

    private Boolean isLogin = false;
    @Autowired
    private BoardDataProvider provider;


    public SlrBoardDataGrabService(String $id, String $pwd) {
        this.id = $id;
        this.pwd = $pwd;
    }

    public SlrBoardDataGrabService() {
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
                    .map(tds -> this.provider.getInstance(BoardDataProvider.BoardType.SLR, tds));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
