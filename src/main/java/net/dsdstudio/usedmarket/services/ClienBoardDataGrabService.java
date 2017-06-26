package net.dsdstudio.usedmarket.services;

import net.dsdstudio.usedmarket.utils.Util;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * usedmarket-collector net.dsdstudio.usedmarket.services
 *
 * @author : bhkim
 * @since : 2015-01-22 오후 10:38
 */
@Service
public class ClienBoardDataGrabService implements GrabService {
    @Value("${clien.id}")
    private String id;
    @Value("${clien.pwd}")
    private String pwd;

    private final String loginUrl = "https://www.clien.net/cs2/bbs/login_check.php";
    private final String bbsUrl = "http://www.clien.net/cs2/bbs/board.php?bo_table=sold&sca=[%ED%8C%90%EB%A7%A4]";
    private final String paramId = "mb_id";
    private final String paramPwd = "mb_password";
    private Boolean isLogin = false;

    @Autowired
    private BoardDataProvider provider;

    public ClienBoardDataGrabService() {
    }

    @Override
    @PostConstruct
    public void login() {
        System.out.println("CLIEN LOGIN => " + this.id);
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
    public Boolean authorized() {
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
                    .map(tds -> this.provider.getInstance(BoardDataProvider.BoardType.CLIEN, tds));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
